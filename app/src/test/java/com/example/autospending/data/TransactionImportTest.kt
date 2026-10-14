package com.example.autospending.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TransactionImportTest {
    @Test
    fun `round trip export then import parses rows`() {
        val original = listOf(
            TransactionEntity(
                id = 7,
                type = "expense",
                amount = 25_000L,
                occurredAt = 1_720_000_000_000L,
                sourcePackage = "id.dana",
                sourceTitle = "DANA",
                rawText = "bayar",
                merchant = "Warung",
                category = "Food",
                confidence = 0.9f,
                contentHash = "hash-import-1",
            )
        )
        val json = TransactionExport.toJson(original, appVersionName = "0.3.0")
        val parsed = TransactionImport.parseTransactions(json)
        assertThat(parsed).hasSize(1)
        assertThat(parsed[0].amount).isEqualTo(25_000L)
        assertThat(parsed[0].contentHash).isEqualTo("hash-import-1")
        assertThat(parsed[0].merchant).isEqualTo("Warung")
        assertThat(parsed[0].type).isEqualTo("expense")
    }
}
