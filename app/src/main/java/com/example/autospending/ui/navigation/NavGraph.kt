package com.example.autospending.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.autospending.data.HealthSnapshot
import com.example.autospending.data.RawLogEntity
import com.example.autospending.data.TransactionEntity
import com.example.autospending.ui.AppUiState
import com.example.autospending.ui.ChatMessage
import com.example.autospending.ui.analytics.TimeRange
import com.example.autospending.ui.screens.AuditScreen
import com.example.autospending.ui.screens.BudgetScreen
import com.example.autospending.ui.screens.HomeScreen
import com.example.autospending.ui.screens.OemBatteryScreen
import com.example.autospending.ui.screens.RawLogScreen
import com.example.autospending.ui.screens.TanyaAiScreen
import com.example.autospending.ui.screens.TransactionsScreen

@Composable
fun AutoSpendingNavHost(
    navController: NavHostController,
    state: AppUiState,
    chatMessages: List<ChatMessage>,
    isAiLoading: Boolean,
    apiKey: String,
    parsersEnabled: Boolean,
    diagnosticLogAll: Boolean,
    healthSnapshot: HealthSnapshot,
    isExporting: Boolean,
    hasNotificationAccess: Boolean,
    rawLogEntries: List<RawLogEntity>,
    onTimeRange: (TimeRange) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    onOpenBudget: () -> Unit,
    onClearFilters: () -> Unit,
    onAddManual: (String, String, String, String) -> Unit,
    onUpdate: (TransactionEntity, String, String, String, String) -> Unit,
    onDelete: (TransactionEntity) -> Unit,
    onSetBudget: (String) -> Unit,
    onTypeFilter: (String?) -> Unit,
    onCategoryFilter: (String?) -> Unit,
    onSendMessage: (String) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onClearApiKey: () -> Unit,
    onClearChat: () -> Unit,
    onToggleParsers: (Boolean) -> Unit,
    onToggleDiagnosticLog: (Boolean) -> Unit,
    onExport: () -> Unit,
    onImport: () -> Unit,
    onOpenNotificationAccess: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                state = state,
                onTimeRange = onTimeRange,
                onCategoryClick = onCategoryClick,
                onSeeAll = onSeeAll,
                onClearFilters = onClearFilters,
                onOpenBudget = onOpenBudget,
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(
                state = state,
                filterSummary = state.filterSummary,
                onAddManual = onAddManual,
                onUpdate = onUpdate,
                onDelete = onDelete,
                onTypeFilter = onTypeFilter,
                onCategoryFilter = onCategoryFilter,
                onClearFilters = onClearFilters,
            )
        }
        composable(Screen.Audit.route) {
            AuditScreen(
                state = state,
                parsersEnabled = parsersEnabled,
                diagnosticLogAll = diagnosticLogAll,
                healthSnapshot = healthSnapshot,
                isExporting = isExporting,
                hasNotificationAccess = hasNotificationAccess,
                apiKey = apiKey,
                onToggleParsers = onToggleParsers,
                onToggleDiagnosticLog = onToggleDiagnosticLog,
                onSaveApiKey = onSaveApiKey,
                onClearApiKey = onClearApiKey,
                onOpenNotificationAccess = onOpenNotificationAccess,
                onExport = onExport,
                onImport = onImport,
                onOpenRawLog = { navController.navigate(Screen.RawLog.route) },
                onOpenOemBattery = { navController.navigate(Screen.OemBattery.route) },
            )
        }
        composable(Screen.Budget.route) {
            BudgetScreen(state, onSetBudget)
        }
        composable(Screen.TanyaAi.route) {
            TanyaAiScreen(
                chatMessages = chatMessages,
                isAiLoading = isAiLoading,
                apiKey = apiKey,
                onSendMessage = onSendMessage,
                onSaveApiKey = onSaveApiKey,
                onClearChat = onClearChat,
            )
        }
        composable(Screen.RawLog.route) {
            RawLogScreen(entries = rawLogEntries)
        }
        composable(Screen.OemBattery.route) {
            OemBatteryScreen()
        }
    }
}
