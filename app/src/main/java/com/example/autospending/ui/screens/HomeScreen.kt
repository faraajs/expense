package com.example.autospending.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.ui.AppUiState
import com.example.autospending.ui.analytics.TimeRange
import com.example.autospending.ui.analytics.displayLabel
import com.example.autospending.ui.components.BudgetRing
import com.example.autospending.ui.components.CategoryBreakdown
import com.example.autospending.ui.components.EmptyState
import com.example.autospending.ui.components.SummaryHero
import com.example.autospending.ui.components.TransactionListItem
import com.example.autospending.ui.components.performLight

@Composable
fun HomeScreen(
    state: AppUiState,
    onTimeRange: (TimeRange) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    onClearFilters: () -> Unit,
    onOpenBudget: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryHero(
            totalExpense = state.totalExpense,
            totalIncome = state.totalIncome
        )

        BudgetRing(model = state.budgetRing)
        TextButton(onClick = onOpenBudget, modifier = Modifier.align(Alignment.End)) {
            Text("Manage budget →")
        }

        Text(
            "Explore range",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(TimeRange.TODAY, TimeRange.LAST_7_DAYS, TimeRange.THIS_MONTH).forEach { range ->
                FilterChip(
                    selected = state.timeRange == range,
                    onClick = {
                        haptics.performLight()
                        onTimeRange(range)
                    },
                    label = { Text(range.displayLabel()) }
                )
            }
        }

        CategoryBreakdown(
            items = state.categoryBreakdown,
            selectedCategory = state.categoryFilter,
            onCategoryClick = {
                haptics.performLight()
                onCategoryClick(it)
            }
        )

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onSeeAll) { Text("See all →") }
        }

        val recent = state.visibleTransactions.take(8)
        if (recent.isEmpty()) {
            EmptyState(
                title = if (state.hasActiveFilters) "Nothing matches this filter" else "No transactions yet",
                message = if (state.hasActiveFilters) {
                    "Try changing the range or tap See all."
                } else {
                    "Bank or digital wallet notifications will appear here after access is enabled."
                },
                actionLabel = if (state.hasActiveFilters) "See all" else null,
                onAction = if (state.hasActiveFilters) onClearFilters else null
            )
        } else {
            recent.forEach {
                TransactionListItem(it, onEdit = {}, onDelete = {}, showActions = false)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
