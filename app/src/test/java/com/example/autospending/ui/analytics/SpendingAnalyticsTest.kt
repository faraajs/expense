package com.example.autospending.ui.analytics

import com.example.autospending.data.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class SpendingAnalyticsTest {

    private val zone: ZoneId = ZoneId.of("Asia/Jakarta")

    private fun tx(
        id: Long,
        type: String,
        amount: Long,
        category: String = "Food",
        source: String = "com.gojek.app",
        occurredAt: Long = 0L
    ) = TransactionEntity(
        id = id,
        type = type,
        amount = amount,
        occurredAt = occurredAt,
        sourcePackage = source,
        sourceTitle = "t",
        rawText = "r",
        merchant = "m",
        category = category,
        confidence = 1f,
        contentHash = "h$id"
    )

    @Test
    fun emptyListYieldsEmptyAggregates() {
        assertTrue(aggregateByCategory(emptyList()).isEmpty())
        assertTrue(filterTransactions(emptyList(), null, null, null).isEmpty())
    }

    @Test
    fun todayBoundsHalfOpenNextMidnight() {
        // 2026-03-03 15:00 Asia/Jakarta
        val now = LocalDate.of(2026, 3, 3).atTime(15, 0).atZone(zone).toInstant().toEpochMilli()
        val bounds = rangeBounds(TimeRange.TODAY, now, zone)
        val start = LocalDate.of(2026, 3, 3).atStartOfDay(zone).toInstant().toEpochMilli()
        val end = LocalDate.of(2026, 3, 4).atStartOfDay(zone).toInstant().toEpochMilli()
        assertEquals(start, bounds.startMillis)
        assertEquals(end, bounds.endMillis)
    }

    @Test
    fun last7DaysOnThirdOfMonthIncludesPriorMonthCalendarDays() {
        // 2026-03-03 12:00 — must include Feb 25..Mar 3 inclusive (7 days)
        val now = LocalDate.of(2026, 3, 3).atTime(12, 0).atZone(zone).toInstant().toEpochMilli()
        val bounds = rangeBounds(TimeRange.LAST_7_DAYS, now, zone)
        val expectedStart = LocalDate.of(2026, 2, 25).atStartOfDay(zone).toInstant().toEpochMilli()
        val expectedEnd = LocalDate.of(2026, 3, 4).atStartOfDay(zone).toInstant().toEpochMilli()
        assertEquals(expectedStart, bounds.startMillis)
        assertEquals(expectedEnd, bounds.endMillis)
        // Not rolling 168h from now
        val rolling = now - 7L * 24 * 60 * 60 * 1000
        assertTrue(bounds.startMillis != rolling)
        assertTrue(bounds.startMillis < LocalDate.of(2026, 3, 1).atStartOfDay(zone).toInstant().toEpochMilli())
    }

    @Test
    fun aggregateByCategoryExcludesIncomeAndSortsDesc() {
        val list = listOf(
            tx(1, "expense", 100, "Food"),
            tx(2, "expense", 50, "Food"),
            tx(3, "expense", 200, "Transport"),
            tx(4, "income", 999, "Food")
        )
        val agg = aggregateByCategory(list)
        assertEquals(2, agg.size)
        assertEquals("Transport", agg[0].category)
        assertEquals(200L, agg[0].amount)
        assertEquals("Food", agg[1].category)
        assertEquals(150L, agg[1].amount)
    }

    @Test
    fun filterCategoryExactCaseSensitive() {
        val list = listOf(tx(1, "expense", 10, "Food"), tx(2, "expense", 10, "food"))
        assertEquals(1, filterTransactions(list, "Food", null, null).size)
        assertEquals(0, filterTransactions(list, "FOOD", null, null).size)
    }

    @Test
    fun filterTypeAndSource() {
        val list = listOf(
            tx(1, "expense", 10, source = "com.gojek.app"),
            tx(2, "income", 10, source = "manual")
        )
        assertEquals(1, filterTransactions(list, null, "expense", null).size)
        assertEquals(1, filterTransactions(list, null, null, "GoPay").size)
        assertEquals(1, filterTransactions(list, null, null, "Manual").size)
    }

    @Test
    fun budgetRingModelCases() {
        val unset = budgetRingModel(0, 0)
        assertEquals(BudgetStatus.UNSET, unset.status)
        assertEquals(0f, unset.visualProgress, 0f)

        val half = budgetRingModel(50_000, 100_000)
        assertEquals(BudgetStatus.AMAN, half.status)
        assertEquals(0.5f, half.visualProgress, 0.001f)

        val warn = budgetRingModel(80_000, 100_000)
        assertEquals(BudgetStatus.HATI_HATI, warn.status)

        val full = budgetRingModel(100_000, 100_000)
        assertEquals(BudgetStatus.OVER, full.status)
        assertEquals(1f, full.visualProgress, 0f)
        assertNull(full.overPercent)
        assertEquals("100%", full.centerLabel)

        val over = budgetRingModel(120_000, 100_000)
        assertEquals(BudgetStatus.OVER, over.status)
        assertEquals(1f, over.visualProgress, 0f)
        assertEquals(20, over.overPercent)
        assertEquals("+20%", over.centerLabel)
    }

    @Test
    fun explorationFilterSummaryCanonical() {
        assertEquals(
            "",
            explorationFilterSummary(TimeRange.THIS_MONTH, null, null, null)
        )
        assertEquals(
            "Rentang: 7 hari · Food · Expense",
            explorationFilterSummary(TimeRange.LAST_7_DAYS, "Food", "expense", null)
        )
    }

    @Test
    fun hasActiveFiltersAndClearPostconditionSemantics() {
        assertFalse(
            hasActiveExplorationFilters(TimeRange.THIS_MONTH, null, null, null)
        )
        assertTrue(
            hasActiveExplorationFilters(TimeRange.LAST_7_DAYS, null, null, null)
        )
        assertTrue(
            hasActiveExplorationFilters(TimeRange.THIS_MONTH, "Food", null, null)
        )
        // clearFilters post-condition shape
        val clearedRange = TimeRange.THIS_MONTH
        val clearedCat: String? = null
        val clearedType: String? = null
        val clearedSource: String? = null
        assertFalse(
            hasActiveExplorationFilters(clearedRange, clearedCat, clearedType, clearedSource)
        )
        assertNull(clearedCat)
    }
}
