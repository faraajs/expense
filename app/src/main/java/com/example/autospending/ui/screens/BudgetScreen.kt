package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.autospending.data.formatIdr
import com.example.autospending.ui.AppUiState
import com.example.autospending.ui.components.BudgetRing
import com.example.autospending.ui.components.performConfirm

@Composable
fun BudgetScreen(state: AppUiState, onSetBudget: (String) -> Unit) {
    var budget by remember { mutableStateOf("") }
    val haptics = LocalHapticFeedback.current
    LaunchedEffect(state.budgetLimit) {
        if (state.budgetLimit > 0 && budget.isBlank()) budget = state.budgetLimit.toString()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Budget bulanan",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        BudgetRing(model = state.budgetRing, ringSize = 180.dp)
        Text(
            "Status: ${state.budgetStatusLabel}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = budget,
            onValueChange = { budget = it },
            label = { Text("Limit IDR") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (budget.any { it.isDigit() }) {
            val preview = budget.filter { it.isDigit() }.toLongOrNull() ?: 0L
            Text("Preview: ${formatIdr(preview)}", style = MaterialTheme.typography.bodyMedium)
        }
        Button(
            onClick = {
                haptics.performConfirm()
                onSetBudget(budget)
            },
            enabled = budget.any { it.isDigit() }
        ) {
            Text("Simpan budget")
        }
        Text("Alert dikirim saat pengeluaran mencapai 80% dan 100% dari budget.")
        if (state.budgetLimit > 0) {
            Text("Terpakai: ${formatIdr(state.totalExpense)}")
            Text("Limit: ${formatIdr(state.budgetLimit)}")
            Text("Sisa: ${formatIdr(state.remainingBudget)}")
        }
        Spacer(Modifier.height(24.dp))
    }
}
