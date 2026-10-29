package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.autospending.data.RecurringExpenseEntity
import com.example.autospending.data.formatIdr

@Composable
fun RecurringExpensesScreen(
    items: List<RecurringExpenseEntity>,
    monthlyTotal: Long,
    onAdd: (String, Long, String) -> Unit,
    onDelete: (RecurringExpenseEntity) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (monthlyTotal > 0) {
                Card(
                    Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recurring total / month", style = MaterialTheme.typography.bodyMedium)
                        Text(formatIdr(monthlyTotal), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (items.isEmpty()) {
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No recurring expenses yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "Add monthly bills such as Netflix, Spotify, or electricity.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        RecurringCard(item, onDelete)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showAdd) {
        AddRecurringDialog(
            onDismiss = { showAdd = false },
            onConfirm = { name, amount, category ->
                onAdd(name, amount, category)
                showAdd = false
            }
        )
    }
}

@Composable
private fun RecurringCard(item: RecurringExpenseEntity, onDelete: (RecurringExpenseEntity) -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.SemiBold)
                Text("${item.category} · ${item.frequency}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatIdr(item.amount) + "/month", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onDelete(item) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddRecurringDialog(onDismiss: () -> Unit, onConfirm: (String, Long, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Bills") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add recurring expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Name (for example: Netflix)") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it.filter { c -> c.isDigit() } },
                    label = { Text("Amount (Rp)") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = category, onValueChange = { category = it },
                    label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim(), amount.toLongOrNull() ?: 0L, category.trim().ifBlank { "Bills" }) },
                enabled = name.trim().isNotBlank() && amount.toLongOrNull() != null && (amount.toLongOrNull() ?: 0L) > 0
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
