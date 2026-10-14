package com.example.autospending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.autospending.ui.theme.ExpenseRed
import com.example.autospending.ui.theme.ExpenseRedSoft
import com.example.autospending.ui.theme.IncomeGreen
import com.example.autospending.ui.theme.IncomeGreenSoft

@Composable
fun TypeToggle(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == "expense",
            onClick = { onTypeChange("expense") },
            label = { Text("Keluar") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ExpenseRedSoft,
                selectedLabelColor = ExpenseRed,
                selectedLeadingIconColor = ExpenseRed,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedType == "expense",
                selectedBorderColor = ExpenseRed,
                borderColor = ExpenseRed.copy(alpha = 0.4f),
            ),
        )
        FilterChip(
            selected = selectedType == "income",
            onClick = { onTypeChange("income") },
            label = { Text("Masuk") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = IncomeGreenSoft,
                selectedLabelColor = IncomeGreen,
                selectedLeadingIconColor = IncomeGreen,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedType == "income",
                selectedBorderColor = IncomeGreen,
                borderColor = IncomeGreen.copy(alpha = 0.4f),
            ),
        )
    }
}
