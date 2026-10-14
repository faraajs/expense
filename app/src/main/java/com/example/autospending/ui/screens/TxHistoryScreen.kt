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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.autospending.data.TransactionEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TxHistoryScreen(
    transactions: List<TransactionEntity>,
    onDelete: (TransactionEntity) -> Unit,
    onDeleteAll: () -> Unit
) {
    var toDelete by remember { mutableStateOf<TransactionEntity?>(null) }
    var showDeleteAll by remember { mutableStateOf(false) }
    val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val idrFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${transactions.size} transaksi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (transactions.isNotEmpty()) {
                OutlinedButton(
                    onClick = { showDeleteAll = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus semua") }
            }
        }

        if (transactions.isEmpty()) {
            Column(
                Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Belum ada transaksi", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${if (tx.type == "expense") "➖" else "➕"} Rp${idrFmt.format(tx.amount)}",
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "${tx.merchant ?: "—"} · ${tx.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    fmt.format(Date(tx.occurredAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            TextButton(
                                onClick = { toDelete = tx },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) { Text("Hapus") }
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }

    toDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Hapus transaksi?") },
            text = { Text("Rp${idrFmt.format(tx.amount)} dari ${tx.merchant ?: tx.sourcePackage} akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = { onDelete(tx); toDelete = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus") }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Batal") } }
        )
    }

    if (showDeleteAll) {
        AlertDialog(
            onDismissRequest = { showDeleteAll = false },
            title = { Text("Hapus semua transaksi?") },
            text = { Text("Ini akan menghapus ${transactions.size} transaksi secara permanen. Tidak bisa dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = { onDeleteAll(); showDeleteAll = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus semua") }
            },
            dismissButton = { TextButton(onClick = { showDeleteAll = false }) { Text("Batal") } }
        )
    }
}
