package com.example.autospending.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.autospending.ui.theme.ExpenseRed
import com.example.autospending.ui.theme.ExpenseRedSoft
import com.example.autospending.ui.theme.IncomeGreen
import com.example.autospending.ui.theme.IncomeGreenSoft

@Composable
fun FilterChipRow(
    typeFilter: String?,
    categoryFilter: String?,
    categories: List<String>,
    onTypeSelected: (String?) -> Unit,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = typeFilter == null,
            onClick = { onTypeSelected(null) },
            label = { Text("Semua") }
        )
        FilterChip(
            selected = typeFilter == "expense",
            onClick = { onTypeSelected(if (typeFilter == "expense") null else "expense") },
            label = { Text("Keluar") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ExpenseRedSoft,
                selectedLabelColor = ExpenseRed,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = typeFilter == "expense",
                selectedBorderColor = ExpenseRed,
                borderColor = ExpenseRed.copy(alpha = 0.35f),
            ),
        )
        FilterChip(
            selected = typeFilter == "income",
            onClick = { onTypeSelected(if (typeFilter == "income") null else "income") },
            label = { Text("Masuk") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IncomeGreenSoft,
                selectedLabelColor = IncomeGreen,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = typeFilter == "income",
                selectedBorderColor = IncomeGreen,
                borderColor = IncomeGreen.copy(alpha = 0.35f),
            ),
        )
        categories.forEach { cat ->
            FilterChip(
                selected = categoryFilter == cat,
                onClick = { onCategorySelected(if (categoryFilter == cat) null else cat) },
                label = { Text(cat) },
                modifier = Modifier.padding(end = 0.dp)
            )
        }
    }
}

@Composable
fun ActiveFilterSummaryBar(
    summary: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(summary, modifier = Modifier.weight(1f))
        TextButton(onClick = onClear) {
            Text("Tampilkan semua")
        }
    }
}
