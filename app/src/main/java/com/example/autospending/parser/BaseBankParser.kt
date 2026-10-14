package com.example.autospending.parser

import com.example.autospending.data.TransactionEntity
import java.util.Locale

/**
 * Base class for all bank/e-wallet parsers.
 * Subclasses define package prefixes and optional custom amount/merchant logic.
 * Credit-first evaluation: if we can't confidently classify direction, we return null (never guess).
 *
 * Pipeline:
 *  1. Pre-filter non-transactions (OTP, promo, PIN, etc.)
 *  2. Extract amount — reject if none found
 *  3. Classify direction (credit-first) — reject if ambiguous
 *  4. Extract merchant
 *  5. Infer category
 *  6. Compute dedup fingerprint (SHA-256)
 *  7. Return TransactionEntity
 */
abstract class BaseBankParser {
    /** Package prefixes this parser handles (e.g., ["com.gojek.gopay", "com.gojek.app"]). */
    abstract val packagePrefixes: List<String>

    /** Override to define source "family" for dedup (e.g., Gojek apps collapse to "com.gojek"). */
    open val dedupFamily: String get() = packagePrefixes.first()

    fun handles(packageName: String): Boolean =
        packagePrefixes.any { packageName.startsWith(it) }

    fun parse(payload: NotificationPayload): TransactionEntity? {
        val raw = buildRaw(payload)
        if (raw.isBlank()) return null
        if (isNonTransaction(raw)) return null
        if (!hasTransactionIntent(raw)) return null

        val amount = extractAmount(raw) ?: return null
        val type = amount.signedType ?: inferType(raw) ?: return null
        val merchant = extractMerchant(raw)
        val category = inferCategory(raw)
        val confidence = computeConfidence(raw, type, merchant)
        val hash = contentHash(payload, type, amount.value)

        return TransactionEntity(
            type = type,
            amount = amount.value,
            occurredAt = payload.postedAt,
            sourcePackage = payload.packageName,
            sourceTitle = payload.title.take(120),
            rawText = raw.take(1000),
            merchant = merchant,
            category = category,
            confidence = confidence,
            contentHash = hash
        )
    }

    // --- Overridable hooks ---

    /** Subclasses can override for custom amount extraction. Default uses shared AmountParseUtils. */
    open fun extractAmount(raw: String): ParsedAmount? = AmountParseUtils.extract(raw)

    /** Subclasses can override for custom merchant extraction. */
    open fun extractMerchant(raw: String): String? = genericMerchant(raw)

    // --- Fixed pipeline steps ---

    private fun buildRaw(payload: NotificationPayload): String =
        listOf(payload.title, payload.text)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .trim()

    /** Returns true if this looks like an OTP, promo, marketing, or login notif — never a transaction. */
    private fun isNonTransaction(raw: String): Boolean {
        val lower = raw.lowercase(Locale.ROOT)
        return NON_TX_PHRASES.any { lower.contains(it) }
    }

    /** Returns true if there's any transaction signal (amount + keyword, or direction keyword). */
    private fun hasTransactionIntent(raw: String): Boolean {
        val lower = raw.lowercase(Locale.ROOT)
        val hasAmount = AmountParseUtils.extract(raw) != null
        val hasKeyword = TX_INDICATORS.any { lower.contains(it) }
        return hasAmount && hasKeyword
    }

    /**
     * Credit-first direction inference.
     * If credit score > 0 → income.
     * If debit score > 0 AND credit score == 0 → expense.
     * If both zero OR equal → null (ambiguous, will cause parse fail rather than guess).
     */
    private fun inferType(raw: String): String? {
        val lower = raw.lowercase(Locale.ROOT)
        val creditScore = CREDIT_PHRASES.count { lower.contains(it) }
        val debitScore = DEBIT_PHRASES.count { lower.contains(it) }
        return when {
            creditScore > 0 && creditScore >= debitScore -> "income"
            debitScore > 0 && creditScore == 0 -> "expense"
            else -> null // ambiguous — never guess
        }
    }

    private fun inferCategory(raw: String): String {
        val lower = raw.lowercase(Locale.ROOT)
        return CATEGORY_HINTS.entries.firstOrNull { (_, words) ->
            words.any { lower.contains(it) }
        }?.key ?: "Other"
    }

    private fun computeConfidence(raw: String, type: String, merchant: String?): Float {
        var score = 0.62f
        if (raw.contains("rp", ignoreCase = true) || raw.contains("idr", ignoreCase = true)) score += 0.16f
        if (merchant != null) score += 0.08f
        if (type == "income") score += 0.02f
        return score.coerceAtMost(0.95f)
    }

    private fun contentHash(payload: NotificationPayload, type: String, amount: Long): String {
        val normalizedText = normalizeForDedup(payload.title, payload.text)
        return TransactionFingerprintGenerator.generateHash(
            packageFamily = dedupFamily,
            type = type,
            amount = amount,
            normalizedText = normalizedText,
            postedAtMs = payload.postedAt,
        )
    }

    private fun normalizeForDedup(title: String, text: String): String {
        val joined = listOf(title, text)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .lowercase(Locale.ROOT)
            .replace(Regex("""\s+"""), " ")
            .trim()
        return joined
            .replace(Regex("""(?i)\b(?:rp|idr)\s*[0-9][0-9.,]*"""), "AMOUNT")
            .replace(Regex("""(?i)\b[0-9][0-9.,]*\s*(?:rp|idr)\b"""), "AMOUNT")
            .replace(Regex("""\b\d{1,2}[:.]\d{2}\b"""), "TIME")
            .replace(Regex("""\b\d{1,2}[-/]\d{1,2}[-/]\d{2,4}\b"""), "DATE")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    /** Generic merchant extractor — works for most banks. Subclasses can override. */
    private fun genericMerchant(raw: String): String? {
        val stopWords = setOf(
            "berhasil", "pembayaran", "transaksi", "kamu", "anda", "gopay", "saldo",
            "shopee", "dana", "ovo", "baru", "aja", "saja", "pake", "pakai"
        )
        for (pattern in MERCHANT_PATTERNS) {
            val match = pattern.find(raw) ?: continue
            val candidate = match.groupValues.getOrNull(1)?.trim(' ', '.', ',', '-', '_') ?: continue
            val cleaned = when {
                candidate.contains("SpayLater", ignoreCase = true) -> "Shopee PayLater"
                else -> candidate
            }
            val firstToken = cleaned.lowercase(Locale.ROOT).split(Regex("""\s+""")).firstOrNull().orEmpty()
            if (cleaned.length >= 2 && firstToken !in stopWords) {
                return cleaned.take(40)
            }
        }
        return null
    }

    companion object {
        val NON_TX_PHRASES = listOf(
            "otp", "kode rahasia", "password", " pin ", "promo", "voucher", "diskon",
            "jangan berikan", "login", "verifikasi", "masuk dengan", "sign in",
            "jatuh tempo", "bayar sebelum", "ajukan", "pinjaman", "limit kredit",
            "pending", "menunggu", "waiting", "authentication"
        )

        val TX_INDICATORS = listOf(
            "berhasil", "transaksi", "pembayaran", "pembelian", "bayar", "dibayar",
            "terbayar", "lunas", "debit", "debet", "kredit", "transfer",
            "dana masuk", "uang masuk", "cashback", "menerima", "diterima",
            "sent", "paid", "payment", "purchase", "received", "withdraw"
        )

        val CREDIT_PHRASES = listOf(
            "dana masuk", "uang masuk", "kredit", "credit", "saldo masuk",
            "diterima", "menerima", "transfer dari", "transfer berhasil dari",
            "received", "refund diterima", "cashback", "top up berhasil",
            "isi saldo berhasil", "terima gopay", "menerima gopay", "masuk ke"
        )

        val DEBIT_PHRASES = listOf(
            "berhasil bayar", "bayar berhasil", "baru aja bayar", "baru saja bayar",
            "debit", "debet", "bayar", "pembayaran", "dibayar", "terbayar",
            "pembelian", "transaksi", "transfer ke", "transfer berhasil ke",
            "kirim", "terkirim", "sent", "payment", "paid", "purchase",
            "withdraw", "tarik tunai", "spaylater berhasil dibayar",
            "pake gopay", "pakai gopay", "gopay saldo"
        )

        val CATEGORY_HINTS = mapOf(
            "Food" to listOf("makan", "resto", "restaurant", "kopi", "coffee", "cafe", "gofood", "grabfood"),
            "Transport" to listOf("gojek", "grab", "taxi", "tol", "parkir", "transport", "ojek", "bus", "kereta"),
            "Groceries" to listOf("mart", "supermarket", "alfamart", "indomaret", "grocery", "hypermart"),
            "Bills" to listOf("pln", "listrik", "air", "pdam", "tagihan", "internet", "pulsa", "spaylater", "paylater"),
            "Shopping" to listOf("shopee", "tokopedia", "lazada", "mall", "belanja", "bukalapak"),
            "Transfer" to listOf("transfer", "kirim", "terima", "send")
        )

        private val MERCHANT_PATTERNS = listOf(
            Regex("""(?i)bayar\s+(?:rp|idr)\.?\s*[0-9][0-9.,]*\s+ke\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{1,40}?)(?:\s+pake|\s+pakai|\s+dengan|\s+via|\s+di|\.|$)"""),
            Regex("""(?i)Pembayaran\s+([A-Za-z0-9][A-Za-z0-9 ._-]{2,32})\s+kamu"""),
            Regex("""(?i)\b(?:di|ke|dari|to|from|at)\s+([A-Za-z0-9][A-Za-z0-9 ._'&-]{2,32})"""),
            Regex("""(?i)\bmerchant[:\s]+([A-Za-z0-9][A-Za-z0-9 ._-]{2,32})""")
        )
    }
}

data class ParsedAmount(val value: Long, val signedType: String?)
