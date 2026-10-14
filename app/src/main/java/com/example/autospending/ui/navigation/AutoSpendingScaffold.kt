package com.example.autospending.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.autospending.BuildConfig
import com.example.autospending.ui.components.PermissionBanners
import com.example.autospending.ui.components.performLight

@Composable
fun AutoSpendingScaffold(
    monthKey: String,
    selectedTab: AppTab,
    onSelectTab: (AppTab) -> Unit,
    snackbarHostState: SnackbarHostState,
    hasNotificationAccess: Boolean,
    isBatteryUnrestricted: Boolean,
    onOpenNotificationAccess: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    content: @Composable (AppTab) -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val tabs = AppTab.entriesInOrder

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            haptics.performLight()
                            onSelectTab(tab)
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                "Expense",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Bulan $monthKey · v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            PermissionBanners(
                hasNotificationAccess = hasNotificationAccess,
                isBatteryUnrestricted = isBatteryUnrestricted,
                onOpenNotificationAccess = onOpenNotificationAccess,
                onOpenBatterySettings = onOpenBatterySettings
            )
            if (!hasNotificationAccess || !isBatteryUnrestricted) {
                Spacer(Modifier.height(8.dp))
            }

            Box(Modifier.fillMaxSize()) {
                content(selectedTab)
            }
        }
    }
}
