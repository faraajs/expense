package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.autospending.data.HealthSnapshot

@Composable
fun SettingsScreen(
    hasNotificationAccess: Boolean,
    parsersEnabled: Boolean,
    diagnosticLogAll: Boolean,
    apiKey: String,
    healthSnapshot: HealthSnapshot,
    onToggleParsers: (Boolean) -> Unit,
    onToggleDiagnosticLog: (Boolean) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    onOpenNotificationAccess: () -> Unit,
    onOpenRawLog: () -> Unit,
    onOpenOemBattery: () -> Unit,
    onOpenTxHistory: () -> Unit
) {
    var apiKeyDraft by remember(apiKey) { mutableStateOf(apiKey) }
    var showKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // NLS Status
        SectionCard(title = "Notification status") {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        if (hasNotificationAccess) "✅ Access enabled" else "❌ Access not enabled",
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!hasNotificationAccess) {
                        Text(
                            "The app cannot read notifications",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                if (!hasNotificationAccess) {
                    TextButton(onClick = onOpenNotificationAccess) { Text("Enable") }
                }
            }
        }

        // Feature flags
        SectionCard(title = "Parser mode") {
            SettingsToggle(
                title = "Parser enabled",
                subtitle = "Disable for Phase 0 (record only, do not parse)",
                checked = parsersEnabled,
                onCheckedChange = onToggleParsers
            )
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            SettingsToggle(
                title = "Log all notifications",
                subtitle = "Record all packages in raw_log (not only the whitelist)",
                checked = diagnosticLogAll,
                onCheckedChange = onToggleDiagnosticLog
            )
        }

        // Gemini API Key
        SectionCard(title = "Gemini AI") {
            Text(
                "API key for AI chat and automatic categorization. Get one at aistudio.google.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKeyDraft,
                onValueChange = { apiKeyDraft = it },
                label = { Text("API Key (AIza...)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { showKey = !showKey }) {
                        Icon(
                            if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showKey) "Hide" else "Show"
                        )
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSaveApiKey(apiKeyDraft) },
                    enabled = apiKeyDraft.trim().isNotBlank()
                ) { Text("Save") }
                OutlinedButton(onClick = {
                    apiKeyDraft = ""
                    onClearApiKey()
                }) { Text("Clear") }
            }
        }

        // Health counters
        if (healthSnapshot.packages.isNotEmpty()) {
            SectionCard(title = "Parser status by source") {
                Text(
                    if (healthSnapshot.listenerConnected) "🟢 Listener connected" else "🔴 Listener disconnected",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (healthSnapshot.listenerConnected)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(8.dp))
                healthSnapshot.packages.take(10).forEach { pkg ->
                    val shortName = pkg.packageName.substringAfterLast(".")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(shortName, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "✅${pkg.parsed} ⏭️${pkg.skipped} ❌${pkg.failed}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Navigation links
        SectionCard(title = "Tools") {
            TextButton(onClick = onOpenRawLog, modifier = Modifier.fillMaxWidth()) {
                Text("📋 View raw notification log →")
            }
            TextButton(onClick = onOpenOemBattery, modifier = Modifier.fillMaxWidth()) {
                Text("🔋 OEM battery guide →")
            }
            TextButton(onClick = onOpenTxHistory, modifier = Modifier.fillMaxWidth()) {
                Text("🗑️ Manage transaction history →")
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            content()
        }
    }
}

@Composable
private fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
