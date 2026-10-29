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
            Text("Notification access is not enabled", fontWeight = FontWeight.SemiBold)
            Text(
                "Enable notification access for Expense. " +
                    "If it is enabled but transactions are not recorded, turn it off and on again."
            )
            Button(onClick = onOpenNotificationAccess) {
                Text("Open settings")
            }
        }
    }
}

@Composable
fun BatteryCard(onOpenBatterySettings: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Battery optimization is still active", fontWeight = FontWeight.SemiBold)
            Text(
                "Samsung often stops notification listeners in the background. " +
                    "Allow this app to run without battery restrictions so automatic tracking stays active."
            )
            Button(onClick = onOpenBatterySettings) {
                Text("Allow without restrictions")
            }
        }
    }
}
