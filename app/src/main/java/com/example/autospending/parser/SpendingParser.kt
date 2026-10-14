package com.example.autospending.parser

import com.example.autospending.data.TransactionEntity
import java.util.Locale
import kotlin.math.min

/**
 * LIVE production parser for notification ingestion.
 * Uses shared [AmountParseUtils] + [TransactionFingerprintGenerator] + [TransactionDirectionClassifier].
 */
class SpendingParser(
    private val rules: List<ParserRule> = DefaultParserRules.rules
) {
    fun parse(payload: NotificationPayload): TransactionEntity? {
        val rule = rules.firstOrNull { it.enabled && payload.packageName.startsWith(it.sourcePackage) }
            ?: return null
        val raw = listOf(payload.title, payload.text)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .trim()
        if (raw.isBlank()) return null
        if (rule.ignorePatterns.any { it.containsMatchIn(raw) }) return null
        if (!hasTransactionIntent(raw, rule)) return null

        val parsedAmount = extractAmount(raw, rule.amountPatterns) ?: return null
        val type = parsedAmount.signedType
            ?: TransactionDirectionClassifier.classify(
                raw,
                rule.directionKeywords.expense,
                rule.directionKeywords.income,
            )
            ?: return null
        val category = inferCategory(raw, rule.categoryHints)
        val merchant = inferMerchant(raw, rule)
        val confidence = confidence(raw, type, merchant)
        val hash = contentHash(payload, type, parsedAmount.amount)

        return TransactionEntity(
            type = type,
            amount = parsedAmount.amount,
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

    private data class ParsedAmount(
        val amount: Long,
        val signedType: String?
    )

    private fun extractAmount(raw: String, patterns: List<Regex>): ParsedAmount? {
        // Prefer shared util first (Rp/IDR + sign).
        AmountParseUtils.extract(raw)?.let { shared ->
            return ParsedAmount(shared.value, shared.signedType)
        }
        for (pattern in patterns) {
            val match = pattern.find(raw) ?: continue
            val sign = match.groupValues.getOrNull(1).orEmpty()
            val candidate = match.groupValues.getOrNull(2).orEmpty()
            val amount = AmountParseUtils.normalize(candidate).toLongOrNull()
            if (amount != null && amount > 0) {
                val signedType = when (sign) {
                    "+" -> "income"
                    "-" -> "expense"
                    else -> null
                }
                return ParsedAmount(amount, signedType)
            }
        }
        return null
    }

    private fun hasTransactionIntent(raw: String, rule: ParserRule): Boolean {
        val lowered = raw.lowercase(Locale.ROOT)
        return rule.transactionIndicators.any { lowered.contains(it) } ||
            rule.directionKeywords.expense.any { lowered.contains(it) } ||
            rule.directionKeywords.income.any { lowered.contains(it) }
    }

    private fun inferCategory(raw: String, hints: Map<String, List<String>>): String {
        val lowered = raw.lowercase()
        return hints.entries.firstOrNull { (_, words) ->
            words.any { lowered.contains(it) }
        }?.key ?: "Other"
    }

    private fun inferMerchant(raw: String, rule: ParserRule): String? {
        val stopWords = setOf(
            "berhasil", "pembayaran", "transaksi", "kamu", "anda", "gopay", "saldo",
            "shopee", "dana", "ovo", "baru", "aja", "saja", "pake", "pakai"
        )
        for (pattern in rule.merchantPatterns) {
            val match = pattern.find(raw) ?: continue
            val candidate = match.groupValues.getOrNull(1)?.trim(' ', '.', ',', '-', '_') ?: continue
            val cleaned = when {
                candidate.contains("SpayLater", ignoreCase = true) -> "Shopee PayLater"
                else -> candidate
            }
            val firstToken = cleaned.lowercase(Locale.ROOT).split(Regex("""\s+""")).firstOrNull().orEmpty()
            if (cleaned.length >= 2 && firstToken !in stopWords) {
                return cleaned.substring(0, min(cleaned.length, 40))
            }
        }
        return null
    }

    private fun confidence(raw: String, type: String, merchant: String?): Float {
        var score = 0.62f
        if (raw.contains("rp", ignoreCase = true) || raw.contains("idr", ignoreCase = true)) score += 0.16f
        if (merchant != null) score += 0.08f
        if (type == "income") score += 0.02f
        return min(score, 0.95f)
    }

    private fun contentHash(payload: NotificationPayload, type: String, amount: Long): String {
        val normalizedText = normalizeForDuplicateCheck(payload.title, payload.text)
        return TransactionFingerprintGenerator.generateHash(
            packageFamily = PackageCatalog.dedupFamily(payload.packageName),
            type = type,
            amount = amount,
            normalizedText = normalizedText,
            postedAtMs = payload.postedAt,
        )
    }

    private fun normalizeForDuplicateCheck(title: String, text: String): String {
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
}
