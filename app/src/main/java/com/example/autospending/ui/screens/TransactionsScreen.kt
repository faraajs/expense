package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.autospending.data.TransactionEntity
import com.example.autospending.ui.AppUiState
import com.example.autospending.ui.components.ActiveFilterSummaryBar
import com.example.autospending.ui.components.EditTransactionDialog
import com.example.autospending.ui.components.EmptyState
import com.example.autospending.ui.components.FilterChipRow
import com.example.autospending.ui.components.TransactionListItem
import com.example.autospending.ui.components.TypeToggle
import com.example.autospending.ui.components.performConfirm
import com.example.autospending.ui.components.performLight

@Composable
fun TransactionsScreen(
    state: AppUiState,
    filterSummary: String,
    onAddManual: (String, String, String, String) -> Unit,
    onUpdate: (TransactionEntity, String, String, String, String) -> Unit,
    onDelete: (TransactionEntity) -> Unit,
    onTypeFilter: (String?) -> Unit,
    onCategoryFilter: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    var editing by remember { mutableStateOf<TransactionEntity?>(null) }
    val haptics = LocalHapticFeedback.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ManualEntryCard(
                onAddManual = { type, amount, merchant, category ->
                    haptics.performConfirm()
                    onAddManual(type, amount, merchant, category)
                }
            )
        }
        if (state.hasActiveFilters) {
            item {
                ActiveFilterSummaryBar(
                    summary = filterSummary.ifBlank { "Active filter" },
                    onClear = {
                        haptics.performLight()
                        onClearFilters()
                    }
                )
            }
        }
        item {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        item {
            FilterChipRow(
                typeFilter = state.typeFilter,
                categoryFilter = state.categoryFilter,
                categories = state.availableCategories,
                onTypeSelected = {
                    haptics.performLight()
                    onTypeFilter(it)
                },
                onCategorySelected = {
                    haptics.performLight()
                    onCategoryFilter(it)
                }
            )
        }
        item {
            Text(
                "Transaction list",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (state.visibleTransactions.isEmpty()) {
            item {
                EmptyState(
                    title = "No data yet",
                    message = if (state.hasActiveFilters) {
                        "Nothing matches this filter. Tap See all."
                    } else {
                        "There is no data to edit yet."
                    },
                    actionLabel = if (state.hasActiveFilters) "See all" else null,
                    onAction = if (state.hasActiveFilters) onClearFilters else null
                )
            }
        } else {
            items(state.visibleTransactions, key = { it.id }) { transaction ->
                TransactionListItem(
                    transaction = transaction,
                    onEdit = { editing = transaction },
                    onDelete = {
                        haptics.performLight()
                        onDelete(transaction)
                    }
                )
            }
        }
    }

    editing?.let { transaction ->
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { editing = null },
            onSave = { type, amount, merchant, category ->
                haptics.performConfirm()
                onUpdate(transaction, type, amount, merchant, category)
                editing = null
            }
        )
    }
}

@Composable
private fun ManualEntryCard(onAddManual: (String, String, String, String) -> Unit) {
    var type by remember { mutableStateOf("expense") }
    var amount by remember { mutableStateOf("") }
    var merchant by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Other") }

    Card {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Manual entry", fontWeight = FontWeight.SemiBold)
            TypeToggle(type, onTypeChange = { type = it })
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount in IDR") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onAddManual(type, amount, merchant, category)
                    amount = ""
                    merchant = ""
                    category = "Other"
                },
                enabled = amount.any { it.isDigit() }
            ) {
                Text("Save")
            }
        }
    }
}
