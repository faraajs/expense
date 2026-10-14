package com.example.autospending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.autospending.data.TransactionEntity
import com.example.autospending.data.formatDateTime
import com.example.autospending.data.formatIdr
import com.example.autospending.data.toFriendlySourceName
import com.example.autospending.ui.theme.ExpenseRed
import com.example.autospending.ui.theme.IncomeGreen

@Composable
fun TransactionListItem(
    transaction: TransactionEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    showActions: Boolean = true
) {
    val isIncome = transaction.type == "income"
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    Card {
        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    transaction.merchant ?: transaction.category,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${if (isIncome) "+" else "-"}${formatIdr(transaction.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
            Text("${transaction.category} - ${formatDateTime(transaction.occurredAt)}")
            Text(
                "Source: ${transaction.sourcePackage.toFriendlySourceName()}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (showActions) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 12.dp)) {
                        Text("Edit")
                    }
                    TextButton(onClick = onDelete) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun EditTransactionDialog(
    transaction: TransactionEntity,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var type by remember(transaction.id) { mutableStateOf(transaction.type) }
    var amount by remember(transaction.id) { mutableStateOf(transaction.amount.toString()) }
    var merchant by remember(transaction.id) { mutableStateOf(transaction.merchant.orEmpty()) }
    var category by remember(transaction.id) { mutableStateOf(transaction.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit transaksi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TypeToggle(selectedType = type, onTypeChange = { type = it })
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Nominal IDR") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = merchant,
                    onValueChange = { merchant = it },
                    label = { Text("Merchant") }
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(type, amount, merchant, category) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

