package com.example.autospending.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * OEM-specific battery optimization guide.
 * Many OEM Android skins (Xiaomi, Oppo, Vivo) aggressively kill background services.
 * Without unrestricted battery access, the NotificationListenerService may die and miss transactions.
 */
@Composable
fun OemBatteryScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Text(
                "  Pengaturan Baterai OEM",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Beberapa HP (Xiaomi, Oppo, Vivo, Samsung) secara agresif membunuh service background. " +
                "Tanpa pengaturan ini, notifikasi bisa terlewat.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Universal battery optimization
        Button(
            onClick = {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Izinkan baterai tidak dibatasi")
        }

        OemGuideCard(
            title = "📱 Xiaomi / MIUI / HyperOS",
            steps = listOf(
                "Buka Pengaturan → Aplikasi → Expense",
                "Ketuk Baterai → Tidak ada batasan",
                "Buka Keamanan → Mulai otomatis → Aktifkan Expense",
                "Buka Keamanan → Baterai → Aplikasi terproteksi → Aktifkan"
            )
        )

        OemGuideCard(
            title = "📱 Oppo / ColorOS / Realme",
            steps = listOf(
                "Buka Pengaturan → Manajemen aplikasi → Expense",
                "Ketuk Penggunaan baterai → Tidak dioptimalkan",
                "Buka Pengaturan → Manajemen daya → Izin latar belakang → Aktifkan"
            )
        )

        OemGuideCard(
            title = "📱 Vivo / FuntouchOS / OriginOS",
            steps = listOf(
                "Buka Pengaturan → Baterai → Konsumsi baterai latar belakang",
                "Pilih Expense → Izinkan konsumsi latar belakang",
                "Buka iManager → Akselerasi aplikasi → Nonaktifkan untuk Expense"
            )
        )

        OemGuideCard(
            title = "📱 Samsung One UI",
            steps = listOf(
                "Buka Pengaturan → Perawatan perangkat → Baterai",
                "Ketuk Batas penggunaan latar belakang → Aplikasi yang tidak pernah tidur",
                "Tambahkan Expense ke daftar"
            )
        )

        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dontkillmyapp.com"))
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Panduan lengkap: dontkillmyapp.com ↗")
        }
    }
}

@Composable
private fun OemGuideCard(title: String, steps: List<String>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
            steps.forEachIndexed { i, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${i + 1}.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(step, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
