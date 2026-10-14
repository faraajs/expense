package com.example.autospending

import com.example.autospending.ui.analytics.BudgetStatus
import com.example.autospending.ui.analytics.TimeRange
import com.example.autospending.ui.analytics.budgetRingModel
import com.example.autospending.ui.analytics.rangeBounds
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.ZoneId

class AnalyticsTest {

    private val zone = ZoneId.of("Asia/Jakarta")

    @Test
    fun `budgetRingModel UNSET when limit is zero`() {
        val model = budgetRingModel(50_000L, 0L)
        assertThat(model.status).isEqualTo(BudgetStatus.UNSET)
    }

    @Test
    fun `budgetRingModel AMAN when under 80 percent`() {
        val model = budgetRingModel(700_000L, 1_000_000L)
        assertThat(model.status).isEqualTo(BudgetStatus.AMAN)
    }

    @Test
    fun `budgetRingModel HATI_HATI between 80 and 100 percent`() {
        val model = budgetRingModel(850_000L, 1_000_000L)
        assertThat(model.status).isEqualTo(BudgetStatus.HATI_HATI)
    }

    @Test
    fun `budgetRingModel OVER at 100 percent`() {
        val model = budgetRingModel(1_000_000L, 1_000_000L)
        assertThat(model.status).isEqualTo(BudgetStatus.OVER)
    }

    @Test
    fun `budgetRingModel OVER with positive overPercent beyond limit`() {
        val model = budgetRingModel(1_200_000L, 1_000_000L)
        assertThat(model.status).isEqualTo(BudgetStatus.OVER)
        assertThat(model.overPercent).isEqualTo(20)
    }

    @Test
    fun `visualProgress clamped to 1 when over budget`() {
        val model = budgetRingModel(2_000_000L, 1_000_000L)
        assertThat(model.visualProgress).isEqualTo(1f)
    }

    @Test
    fun `rangeBounds TODAY has 24h window`() {
        val nowMs = 1_720_000_000_000L // arbitrary fixed time
        val bounds = rangeBounds(TimeRange.TODAY, nowMs, zone)
        assertThat(bounds.endMillis - bounds.startMillis).isEqualTo(24 * 60 * 60 * 1000L)
    }

    @Test
    fun `rangeBounds LAST_7_DAYS covers 7 calendar days`() {
        val nowMs = 1_720_000_000_000L
        val bounds = rangeBounds(TimeRange.LAST_7_DAYS, nowMs, zone)
        val dayMs = 24 * 60 * 60 * 1000L
        assertThat(bounds.endMillis - bounds.startMillis).isEqualTo(7 * dayMs)
    }
}
