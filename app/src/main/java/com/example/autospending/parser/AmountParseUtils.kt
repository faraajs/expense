package com.example.autospending.parser

import java.util.Locale

/**
 * Shared amount extraction logic.
 * - Prefers Rp/IDR prefixed amounts
 * - Handles Indonesian thousands separators (dot for thousands, comma for decimal)
 * - Rejects fractional rupiah (amounts with 2-decimal comma like 1.234,56 are truncated)
 * - Returns null if no valid positive amount found
 */
object AmountParseUtils {

    private val AMOUNT_PATTERNS = listOf(
        // Signed: +Rp55.000 / -Rp10.000
        Regex("""(?i)([+-]?)\s*\b(?:rp|idr)\.?\s*([0-9][0-9.,]*)"""),
        // Trailing: 55.000 Rp / 1.000.000 IDR
        Regex("""(?i)([+-]?)\s*\b([0-9][0-9.,]*)\s*(?:rp|idr)\b""")
    )

    fun extract(raw: String): ParsedAmount? {
        for (pattern in AMOUNT_PATTERNS) {
            val match = pattern.find(raw) ?: continue
            val sign = match.groupValues.getOrNull(1).orEmpty()
            val candidate = match.groupValues.getOrNull(2).orEmpty()
            val amount = normalize(candidate).toLongOrNull()
            if (amount != null && amount > 0) {
                val signedType = when (sign) {
                    "+" -> "income"
                    "-" -> "expense"
                    else -> null
                }
                return ParsedAmount(value = amount, signedType = signedType)
            }
        }
        return null
    }

    /** Strips Indonesian formatting to extract integer rupiah value. */
    fun normalize(raw: String): String {
        val clean = raw.trim()
        // Detect decimal comma: ends with ",XX" (2 digits) with a dot before the comma
        val decimalComma = clean.lastIndexOf(',').takeIf { it >= 0 }
        val decimalDot = clean.lastIndexOf('.').takeIf { it >= 0 }
        val withoutDecimals = when {
            decimalComma != null && clean.length - decimalComma - 1 == 2 &&
                (decimalDot == null || decimalComma > decimalDot) ->
                clean.substring(0, decimalComma)
            decimalDot != null && clean.length - decimalDot - 1 == 2 && decimalComma == null ->
                clean.substring(0, decimalDot)
            else -> clean
        }
        return withoutDecimals.filter { it.isDigit() }
    }
}
