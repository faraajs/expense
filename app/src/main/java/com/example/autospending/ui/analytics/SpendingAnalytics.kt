package com.example.autospending.ui.analytics

import com.example.autospending.data.TransactionEntity
import com.example.autospending.data.formatIdr
import com.example.autospending.data.toFriendlySourceName
import java.time.Instant
import java.time.ZoneId

enum class TimeRange {
    TODAY,
    LAST_7_DAYS,
    THIS_MONTH
}

/** Shared category catalog for chips, colors, and manual defaults. */
val DEFAULT_CATEGORIES: List<String> = listOf(
    "Food", "Transport", "Groceries", "Bills", "Shopping", "Transfer", "Other"
)

data class CategorySpend(val category: String, val amount: Long)

data class RangeBounds(val startMillis: Long, val endMillis: Long)

enum class BudgetStatus {
    UNSET,
    AMAN,
    HATI_HATI,
    OVER
}

data class BudgetRingModel(
    val rawRatio: Float,
    val visualProgress: Float,
    val overPercent: Int?,
    val status: BudgetStatus,
    val centerLabel: String,
    val centerSubLabel: String
)

/**
 * Half-open calendar bounds [start, end) matching [SpendingDao.observeTransactionsForRange].
 * LAST_7_DAYS = local midnights from today-6 inclusive through tomorrow exclusive (7 calendar days).
 */
fun rangeBounds(range: TimeRange, nowMillis: Long, zone: ZoneId): RangeBounds {
    val localDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    return when (range) {
        TimeRange.TODAY -> {
            val start = localDate.atStartOfDay(zone).toInstant().toEpochMilli()
            val end = localDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            RangeBounds(start, end)
        }
        TimeRange.LAST_7_DAYS -> {
            val start = localDate.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli()
            val end = localDate.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            RangeBounds(start, end)
        }
        TimeRange.THIS_MONTH -> {
            val start = localDate.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val end = localDate.withDayOfMonth(1).plusMonths(1).atStartOfDay(zone).toInstant().toEpochMilli()
            RangeBounds(start, end)
        }
    }
}

fun aggregateByCategory(transactions: List<TransactionEntity>): List<CategorySpend> =
    transactions
        .filter { it.type == "expense" }
        .groupBy { it.category }
        .map { (category, rows) -> CategorySpend(category, rows.sumOf { it.amount }) }
        .sortedByDescending { it.amount }

fun filterTransactions(
    transactions: List<TransactionEntity>,
    category: String?,
    type: String?,
    sourceFriendly: String?
): List<TransactionEntity> {
    return transactions.filter { tx ->
        (category == null || tx.category == category) &&
            (type == null || tx.type == type) &&
            (sourceFriendly == null || tx.sourcePackage.toFriendlySourceName() == sourceFriendly)
    }
}

fun hasActiveExplorationFilters(
    timeRange: TimeRange,
    categoryFilter: String?,
    typeFilter: String?,
    sourceFilter: String?
): Boolean =
    timeRange != TimeRange.THIS_MONTH ||
        categoryFilter != null ||
        typeFilter != null ||
        sourceFilter != null

/** Single canonical filter summary string for Transaksi bar + empty states. */
fun explorationFilterSummary(
    timeRange: TimeRange,
    categoryFilter: String?,
    typeFilter: String?,
    sourceFilter: String?
): String {
    val parts = mutableListOf<String>()
    if (timeRange != TimeRange.THIS_MONTH) {
        parts += "Rentang: ${timeRange.displayLabel()}"
    }
    categoryFilter?.let { parts += it }
    typeFilter?.let { parts += it.replaceFirstChar { c -> c.uppercase() } }
    sourceFilter?.let { parts += it }
    return parts.joinToString(" · ")
}

/**
 * Budget ring semantics — pure; unit-testable.
 * Thresholds default 80/100: AMAN < first, HATI_HATI until 100%, OVER at ≥100%.
 * At exactly 100%: status OVER, center "100%" (not "+0%"); over badge only when rawRatio > 1.
 */
fun budgetRingModel(
    totalExpense: Long,
    budgetLimit: Long,
    thresholds: List<Int> = listOf(80, 100)
): BudgetRingModel {
    if (budgetLimit <= 0) {
        return BudgetRingModel(
            rawRatio = 0f,
            visualProgress = 0f,
            overPercent = null,
            status = BudgetStatus.UNSET,
            centerLabel = "Belum diset",
            centerSubLabel = "Atur budget bulanan"
        )
    }
    val rawRatio = totalExpense.toFloat() / budgetLimit.toFloat()
    val visualProgress = rawRatio.coerceIn(0f, 1f)
    val warnPct = (thresholds.minOrNull() ?: 80) / 100f
    val status = when {
        rawRatio >= 1f -> BudgetStatus.OVER
        rawRatio >= warnPct -> BudgetStatus.HATI_HATI
        else -> BudgetStatus.AMAN
    }
    val overPercent = if (rawRatio > 1f) ((rawRatio - 1f) * 100).toInt() else null
    val remaining = budgetLimit - totalExpense
    val usedPct = (rawRatio * 100).toInt().coerceAtMost(999)
    val centerLabel = when {
        status == BudgetStatus.OVER && overPercent != null -> "+$overPercent%"
        status == BudgetStatus.OVER -> "100%"
        else -> "$usedPct%"
    }
    val centerSubLabel = when (status) {
        BudgetStatus.UNSET -> "Atur budget bulanan"
        BudgetStatus.OVER -> "Over ${formatIdr(-remaining)}"
        else -> "Sisa ${formatIdr(remaining)}"
    }
    return BudgetRingModel(
        rawRatio = rawRatio,
        visualProgress = visualProgress,
        overPercent = overPercent,
        status = status,
        centerLabel = centerLabel,
        centerSubLabel = centerSubLabel
    )
}

fun TimeRange.displayLabel(): String = when (this) {
    TimeRange.TODAY -> "Hari ini"
    TimeRange.LAST_7_DAYS -> "7 hari"
    TimeRange.THIS_MONTH -> "Bulan ini"
}
