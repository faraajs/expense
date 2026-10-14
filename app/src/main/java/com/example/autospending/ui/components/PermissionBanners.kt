package com.example.autospending.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Shell-level setup banners. Keep visible on every tab until conditions are fixed.
 */
@Composable
fun PermissionBanners(
    hasNotificationAccess: Boolean,
    isBatteryUnrestricted: Boolean,
    onOpenNotificationAccess: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!hasNotificationAccess) {
            PermissionCard(onOpenNotificationAccess)
        }
        if (!isBatteryUnrestricted) {
            BatteryCard(onOpenBatterySettings)
        }
    }
}

@Composable
fun PermissionCard(onOpenNotificationAccess: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Notification Access belum aktif", fontWeight = FontWeight.SemiBold)
            Text(
                "Aktifkan akses notifikasi untuk Expense. " +
                    "Kalau sudah aktif tapi tidak mencatat, matikan lalu nyalakan lagi (toggle off/on)."
            )
            Button(onClick = onOpenNotificationAccess) {
                Text("Buka pengaturan")
            }
        }
    }
}

@Composable
fun BatteryCard(onOpenBatterySettings: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Optimasi baterai masih aktif", fontWeight = FontWeight.SemiBold)
            Text(
                "Samsung sering mematikan listener notifikasi di background. " +
                    "Izinkan app ini tanpa batasan baterai supaya auto-track tetap hidup."
            )
            Button(onClick = onOpenBatterySettings) {
                Text("Izinkan tanpa batasan")
            }
        }
    }
}
