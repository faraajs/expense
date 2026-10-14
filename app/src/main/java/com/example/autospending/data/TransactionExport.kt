package com.example.autospending.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Serializes transactions to a portable JSON document for dual-boot / phone wipe backup.
 * Schema versioned so import can evolve later (Phase 4).
 *
 * Pure-Kotlin JSON (no android.org.json) so unit tests run on the JVM.
 */
object TransactionExport {
    const val SCHEMA_VERSION = 1

    fun toJson(
        transactions: List<TransactionEntity>,
        exportedAtMs: Long = System.currentTimeMillis(),
        appVersionName: String = "",
    ): String {
        val sb = StringBuilder()
        sb.append("{\n")
        sb.append("""  "schemaVersion": $SCHEMA_VERSION,""").append('\n')
        sb.append("""  "exportedAt": $exportedAtMs,""").append('\n')
        sb.append("""  "exportedAtIso": "${isoUtc(exportedAtMs)}",""").append('\n')
        sb.append("""  "app": "Expense",""").append('\n')
        sb.append("""  "appVersion": ${jsonString(appVersionName)},""").append('\n')
        sb.append("""  "count": ${transactions.size},""").append('\n')
        sb.append("""  "transactions": [""").append('\n')
        transactions.forEachIndexed { index, tx ->
            sb.append("    {\n")
            sb.append("""      "id": ${tx.id},""").append('\n')
            sb.append("""      "type": ${jsonString(tx.type)},""").append('\n')
            sb.append("""      "amount": ${tx.amount},""").append('\n')
            sb.append("""      "currency": ${jsonString(tx.currency)},""").append('\n')
            sb.append("""      "occurredAt": ${tx.occurredAt},""").append('\n')
            sb.append("""      "occurredAtIso": ${jsonString(isoUtc(tx.occurredAt))},""").append('\n')
            sb.append("""      "sourcePackage": ${jsonString(tx.sourcePackage)},""").append('\n')
            sb.append("""      "sourceTitle": ${jsonString(tx.sourceTitle)},""").append('\n')
            sb.append("""      "merchant": ${jsonNullable(tx.merchant)},""").append('\n')
            sb.append("""      "category": ${jsonString(tx.category)},""").append('\n')
            sb.append("""      "confidence": ${tx.confidence},""").append('\n')
            sb.append("""      "contentHash": ${jsonString(tx.contentHash)},""").append('\n')
            sb.append("""      "createdAt": ${tx.createdAt}""").append('\n')
            // rawText omitted by default — re-export with includeRaw later if needed
            sb.append("    }")
            if (index < transactions.lastIndex) sb.append(',')
            sb.append('\n')
        }
        sb.append("  ]\n")
        sb.append("}\n")
        return sb.toString()
    }

    private fun isoUtc(ms: Long): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        fmt.timeZone = TimeZone.getTimeZone("UTC")
        return fmt.format(Date(ms))
    }

    private fun jsonNullable(value: String?): String =
        if (value == null) "null" else jsonString(value)

    private fun jsonString(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }
}
