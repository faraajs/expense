package com.example.autospending.data

/**
 * Imports transactions from [TransactionExport] JSON (schemaVersion 1).
 * Lightweight string/regex parse to avoid org.json unit-test mocking issues.
 * Dedupes by contentHash via repository insert ignore path.
 */
object TransactionImport {
    data class Result(
        val imported: Int,
        val skipped: Int,
        val errors: Int,
    )

    fun parseTransactions(json: String): List<TransactionEntity> {
        val block = Regex(
            """\{[^{}]*"contentHash"\s*:\s*"([^"]+)"[^{}]*\}""",
            RegexOption.DOT_MATCHES_ALL,
        )
        // Prefer object-by-object extraction for nested-safe-ish export format
        val objects = extractTransactionObjects(json)
        val out = ArrayList<TransactionEntity>(objects.size)
        for (obj in objects) {
            val hash = stringField(obj, "contentHash") ?: continue
            val amount = longField(obj, "amount") ?: continue
            if (amount <= 0L) continue
            val typeRaw = stringField(obj, "type") ?: "expense"
            out.add(
                TransactionEntity(
                    type = if (typeRaw == "income") "income" else "expense",
                    amount = amount,
                    currency = stringField(obj, "currency") ?: "IDR",
                    occurredAt = longField(obj, "occurredAt") ?: System.currentTimeMillis(),
                    sourcePackage = stringField(obj, "sourcePackage") ?: "import",
                    sourceTitle = stringField(obj, "sourceTitle") ?: "Import",
                    rawText = stringField(obj, "rawText") ?: "imported",
                    merchant = stringField(obj, "merchant"),
                    category = stringField(obj, "category") ?: "Other",
                    confidence = floatField(obj, "confidence") ?: 1f,
                    contentHash = hash,
                    createdAt = longField(obj, "createdAt") ?: System.currentTimeMillis(),
                )
            )
        }
        // Fallback: if structured extract failed but contentHash pattern found, still try loose parse
        if (out.isEmpty() && block.containsMatchIn(json)) {
            // no-op; caller sees empty
        }
        return out
    }

    private fun extractTransactionObjects(json: String): List<String> {
        val marker = "\"transactions\""
        val idx = json.indexOf(marker)
        if (idx < 0) return emptyList()
        val arrayStart = json.indexOf('[', idx)
        if (arrayStart < 0) return emptyList()
        val out = mutableListOf<String>()
        var i = arrayStart + 1
        var depth = 0
        var start = -1
        while (i < json.length) {
            when (json[i]) {
                '{' -> {
                    if (depth == 0) start = i
                    depth++
                }
                '}' -> {
                    depth--
                    if (depth == 0 && start >= 0) {
                        out.add(json.substring(start, i + 1))
                        start = -1
                    }
                }
                ']' -> if (depth == 0) break
            }
            i++
        }
        return out
    }

    private fun stringField(obj: String, key: String): String? {
        val m = Regex(""""$key"\s*:\s*"((?:\\.|[^"\\])*)"""").find(obj) ?: return null
        return m.groupValues[1]
            .replace("\\\"", "\"")
            .replace("\\n", "\n")
            .replace("\\\\", "\\")
            .takeIf { it.isNotBlank() }
    }

    private fun longField(obj: String, key: String): Long? {
        val m = Regex(""""$key"\s*:\s*(-?\d+)""").find(obj) ?: return null
        return m.groupValues[1].toLongOrNull()
    }

    private fun floatField(obj: String, key: String): Float? {
        val m = Regex(""""$key"\s*:\s*(-?\d+(?:\.\d+)?)""").find(obj) ?: return null
        return m.groupValues[1].toFloatOrNull()
    }
}
