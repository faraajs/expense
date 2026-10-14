package com.example.autospending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.data.formatIdr
import com.example.autospending.ui.theme.ExpenseRed
import com.example.autospending.ui.theme.IncomeGreen

@Composable
fun SummaryHero(
    totalExpense: Long,
    totalIncome: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HeroTile(
            label = "Pengeluaran",
            value = formatIdr(totalExpense),
            valueColor = ExpenseRed,
            modifier = Modifier.weight(1f)
        )
        HeroTile(
            label = "Pemasukan",
            value = formatIdr(totalIncome),
            valueColor = IncomeGreen,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun HeroTile(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
