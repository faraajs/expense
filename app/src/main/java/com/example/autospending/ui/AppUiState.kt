package com.example.autospending.ui

import com.example.autospending.data.TransactionEntity
import com.example.autospending.ui.analytics.BudgetRingModel
import com.example.autospending.ui.analytics.BudgetStatus
import com.example.autospending.ui.analytics.CategorySpend
import com.example.autospending.ui.analytics.TimeRange
import com.example.autospending.ui.analytics.budgetRingModel
import com.example.autospending.ui.navigation.AppTab

data class AppUiState(
    val monthKey: String,
    val monthTransactions: List<TransactionEntity> = emptyList(),
    val visibleTransactions: List<TransactionEntity> = emptyList(),
    val categoryBreakdown: List<CategorySpend> = emptyList(),
    val availableCategories: List<String> = emptyList(),
    val budgetLimit: Long = 0,
    val totalExpense: Long = 0,
    val totalIncome: Long = 0,
    val budgetRing: BudgetRingModel = budgetRingModel(0, 0),
    val selectedTab: AppTab = AppTab.HOME,
    val timeRange: TimeRange = TimeRange.THIS_MONTH,
    val categoryFilter: String? = null,
    val typeFilter: String? = null,
    val sourceFilter: String? = null,
    val hasActiveFilters: Boolean = false,
    val filterSummary: String = ""
) {
    val remainingBudget: Long = budgetLimit - totalExpense

    val budgetStatusLabel: String
        get() = when (budgetRing.status) {
            BudgetStatus.UNSET -> "Belum diset"
            BudgetStatus.AMAN -> "Aman"
            BudgetStatus.HATI_HATI -> "Hati-hati"
            BudgetStatus.OVER -> "Over"
        }
}
