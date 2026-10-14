package com.example.autospending.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TransactionExportTest {
    @Test
    fun `export json includes schema version and transactions`() {
        val tx = TransactionEntity(
            id = 1,
            type = "expense",
            amount = 15_000L,
            occurredAt = 1_720_000_000_000L,
            sourcePackage = "id.dana",
            sourceTitle = "DANA",
            rawText = "secret raw",
            merchant = "Kopi Kenangan",
            category = "Food",
            confidence = 0.9f,
            contentHash = "abc123",
        )
        val json = TransactionExport.toJson(
            listOf(tx),
            exportedAtMs = 1_721_000_000_000L,
            appVersionName = "0.2.1",
        )
        assertThat(json).contains("\"schemaVersion\": 1")
        assertThat(json).contains("\"count\": 1")
        assertThat(json).contains("\"app\": \"Expense\"")
        assertThat(json).contains("\"amount\": 15000")
        assertThat(json).contains("\"merchant\": \"Kopi Kenangan\"")
        assertThat(json).doesNotContain("secret raw")
        assertThat(json).doesNotContain("rawText")
    }
}
