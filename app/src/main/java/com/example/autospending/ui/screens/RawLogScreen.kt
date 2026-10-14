package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.data.RawLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RawLogScreen(entries: List<RawLogEntity>) {
    if (entries.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Belum ada log notifikasi.\n\nAktifkan diagnostic_log di Settings atau tunggu notifikasi bank masuk.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val fmt = SimpleDateFormat("dd/MM HH:mm:ss", Locale.getDefault())

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "${entries.size} entri terbaru",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(entries, key = { it.id }) { entry ->
            RawLogCard(entry, fmt)
        }
    }
}

@Composable
private fun RawLogCard(entry: RawLogEntity, fmt: SimpleDateFormat) {
    val outcomeColor = when (entry.outcome) {
        "PARSED" -> MaterialTheme.colorScheme.primary
        "PARSE_FAIL" -> MaterialTheme.colorScheme.error
        "SKIPPED_NON_TX" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    entry.packageName.substringAfterLast("."),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    entry.outcome,
                    style = MaterialTheme.typography.labelSmall,
                    color = outcomeColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                fmt.format(Date(entry.receivedAt)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            if (entry.title.isNotBlank()) {
                Text(entry.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
            if (entry.text.isNotBlank()) {
                Text(
                    entry.text.take(200),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!entry.parseError.isNullOrBlank()) {
                Text(
                    "Error: ${entry.parseError}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
