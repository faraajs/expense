package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.autospending.data.HealthSnapshot
import com.example.autospending.data.formatIdr
import com.example.autospending.ui.AppUiState
import com.example.autospending.ui.components.EmptyState

@Composable
fun AuditScreen(
    state: AppUiState,
    parsersEnabled: Boolean = true,
    diagnosticLogAll: Boolean = false,
    healthSnapshot: HealthSnapshot = HealthSnapshot(false, 0L, emptyList()),
    isExporting: Boolean = false,
    hasNotificationAccess: Boolean = false,
    apiKey: String = "",
    onToggleParsers: (Boolean) -> Unit = {},
    onToggleDiagnosticLog: (Boolean) -> Unit = {},
    onSaveApiKey: (String) -> Unit = {},
    onClearApiKey: () -> Unit = {},
    onOpenNotificationAccess: () -> Unit = {},
    onExport: () -> Unit = {},
    onImport: () -> Unit = {},
    onOpenRawLog: () -> Unit = {},
    onOpenOemBattery: () -> Unit = {},
) {
    val rows = state.monthTransactions
    var apiKeyDraft by remember(apiKey) { mutableStateOf(apiKey) }
    var showKey by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Audit & Backup",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Backup data", fontWeight = FontWeight.SemiBold)
                    Text(
                        "Export semua transaksi ke JSON (siap disimpan ke Drive/USB). " +
                            "Penting untuk dual-boot / ganti HP.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onExport,
                        enabled = !isExporting,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isExporting) "Menyiapkan export…" else "Export JSON")
                    }
                    OutlinedButton(
                        onClick = onImport,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Import JSON")
                    }
                    TextButton(onClick = onOpenRawLog, modifier = Modifier.fillMaxWidth()) {
                        Text("📋 Raw log notifikasi →")
                    }
                    TextButton(onClick = onOpenOemBattery, modifier = Modifier.fillMaxWidth()) {
                        Text("🔋 Panduan baterai OEM →")
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Notifikasi & parser", fontWeight = FontWeight.SemiBold)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (hasNotificationAccess) "✅ Akses notifikasi aktif"
                            else "❌ Akses notifikasi belum aktif",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (!hasNotificationAccess) {
                            TextButton(onClick = onOpenNotificationAccess) { Text("Aktifkan") }
                        }
                    }
                    HorizontalDivider()
                    SettingsRow(
                        title = "Parser aktif",
                        subtitle = "Matikan = hanya skip parse (mode aman)",
                        checked = parsersEnabled,
                        onCheckedChange = onToggleParsers
                    )
                    SettingsRow(
                        title = "Log semua package",
                        subtitle = "Diagnostik: rekam paket non-whitelist",
                        checked = diagnosticLogAll,
                        onCheckedChange = onToggleDiagnosticLog
                    )
                    Text(
                        if (healthSnapshot.listenerConnected) "🟢 Listener terhubung"
                        else "🔴 Listener terputus",
                        style = MaterialTheme.typography.bodySmall
                    )
                    healthSnapshot.packages.take(8).forEach { pkg ->
                        val shortName = pkg.packageName.substringAfterLast('.')
                        Text(
                            "$shortName  ✅${pkg.parsed} ⏭️${pkg.skipped} ❌${pkg.failed}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Gemini API key (terenkripsi)", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = apiKeyDraft,
                        onValueChange = { apiKeyDraft = it },
                        label = { Text("AIza…") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (showKey) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onSaveApiKey(apiKeyDraft) },
                            enabled = apiKeyDraft.trim().isNotBlank()
                        ) { Text("Simpan") }
                        OutlinedButton(onClick = {
                            apiKeyDraft = ""
                            onClearApiKey()
                        }) { Text("Hapus") }
                        TextButton(onClick = { showKey = !showKey }) {
                            Text(if (showKey) "Sembunyikan" else "Tampilkan")
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(4.dp))
            Text(
                "Raw transaksi bulan ini",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (rows.isEmpty()) {
            item {
                EmptyState(
                    title = "Belum ada data",
                    message = "Transaksi otomatis muncul setelah notifikasi bank/e-wallet dibaca."
                )
            }
        } else {
            items(rows, key = { it.id }) { transaction ->
                val conf = transaction.confidence
                val confLabel = when {
                    conf >= 0.8f -> "tinggi"
                    conf >= 0.5f -> "sedang"
                    else -> "rendah"
                }
                Card {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "${transaction.sourceTitle} - ${formatIdr(transaction.amount)}",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Confidence ${(conf * 100).toInt()}% ($confLabel) - ${transaction.sourcePackage}"
                        )
                        Text(transaction.rawText, maxLines = 5, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
