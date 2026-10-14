package com.example.autospending

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.service.notification.NotificationListenerService
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.autospending.notifications.SpendingNotificationListenerService
import com.example.autospending.ui.AppViewModel
import com.example.autospending.ui.AppViewModelFactory
import com.example.autospending.ui.UiEvent
import com.example.autospending.ui.navigation.AppTab
import com.example.autospending.ui.navigation.AutoSpendingNavHost
import com.example.autospending.ui.navigation.AutoSpendingScaffold
import com.example.autospending.ui.navigation.Screen
import com.example.autospending.ui.theme.AutoSpendingTheme

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels {
        val app = application as AutoSpendingApp
        AppViewModelFactory(
            repository = app.repository,
            secureApiKeyStore = app.secureApiKeyStore,
            userSettingsStore = app.userSettingsStore,
            listenerHealthTracker = app.listenerHealthTracker,
            appVersionName = runCatching {
                packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
            }.getOrDefault(""),
        )
    }

    private var hasNotificationAccess by mutableStateOf(false)
    private var isBatteryUnrestricted by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as AutoSpendingApp).budgetAlertManager.ensureChannel()
        requestPostNotificationsIfNeeded()
        NotificationListenerService.requestRebind(
            ComponentName(this, SpendingNotificationListenerService::class.java)
        )

        setContent {
            val state by viewModel.uiState.collectAsState()
            val chat by viewModel.chatMessages.collectAsState()
            val isAiLoading by viewModel.isAiLoading.collectAsState()
            val apiKey by viewModel.apiKey.collectAsState()
            val parsersEnabled by viewModel.parsersEnabled.collectAsState()
            val diagnosticLogAll by viewModel.diagnosticLogAll.collectAsState()
            val health by viewModel.healthSnapshot.collectAsState()
            val isExporting by viewModel.isExporting.collectAsState()
            val rawLog by viewModel.rawLogEntries.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()
            val backStack by navController.currentBackStackEntryAsState()
            val currentRoute = backStack?.destination?.route
            val selectedTab = Screen.toAppTab(currentRoute)

            val importLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.OpenDocument()
            ) { uri: Uri? ->
                if (uri == null) return@rememberLauncherForActivityResult
                runCatching {
                    contentResolver.openInputStream(uri)?.use { stream ->
                        val text = stream.bufferedReader(Charsets.UTF_8).readText()
                        viewModel.importTransactionsJson(text)
                    }
                }
            }

            LaunchedEffect(Unit) {
                viewModel.events.collect { event ->
                    when (event) {
                        is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                    }
                }
            }

            // Keep ViewModel tab in sync for any leftover state consumers
            LaunchedEffect(selectedTab) {
                viewModel.selectTab(selectedTab)
            }

            AutoSpendingTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AutoSpendingScaffold(
                        monthKey = state.monthKey,
                        selectedTab = selectedTab,
                        onSelectTab = { tab ->
                            val route = Screen.fromAppTab(tab).route
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        snackbarHostState = snackbarHostState,
                        hasNotificationAccess = hasNotificationAccess,
                        isBatteryUnrestricted = isBatteryUnrestricted,
                        onOpenNotificationAccess = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        },
                        onOpenBatterySettings = { requestBatteryUnrestricted() },
                    ) { _ ->
                        AutoSpendingNavHost(
                            navController = navController,
                            state = state,
                            chatMessages = chat,
                            isAiLoading = isAiLoading,
                            apiKey = apiKey,
                            parsersEnabled = parsersEnabled,
                            diagnosticLogAll = diagnosticLogAll,
                            healthSnapshot = health,
                            isExporting = isExporting,
                            hasNotificationAccess = hasNotificationAccess,
                            rawLogEntries = rawLog,
                            onTimeRange = viewModel::setTimeRange,
                            onCategoryClick = viewModel::toggleCategoryFilter,
                            onSeeAll = {
                                navController.navigate(Screen.Transactions.route) {
                                    launchSingleTop = true
                                }
                            },
                            onOpenBudget = {
                                navController.navigate(Screen.Budget.route) {
                                    launchSingleTop = true
                                }
                            },
                            onClearFilters = viewModel::clearFilters,
                            onAddManual = viewModel::addManualTransaction,
                            onUpdate = viewModel::updateTransaction,
                            onDelete = viewModel::deleteTransaction,
                            onSetBudget = viewModel::setBudget,
                            onTypeFilter = viewModel::setTypeFilter,
                            onCategoryFilter = viewModel::setCategoryFilter,
                            onSendMessage = viewModel::sendChatMessage,
                            onSaveApiKey = viewModel::saveApiKey,
                            onClearApiKey = viewModel::clearApiKey,
                            onClearChat = viewModel::clearChat,
                            onToggleParsers = viewModel::setParsersEnabled,
                            onToggleDiagnosticLog = viewModel::setDiagnosticLogAll,
                            onExport = {
                                viewModel.exportTransactionsToCache(cacheDir) { file ->
                                    if (file != null) shareExportFile(file)
                                }
                            },
                            onImport = {
                                importLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                            },
                            onOpenNotificationAccess = {
                                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                            },
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasNotificationAccess = isNotificationListenerEnabled()
        isBatteryUnrestricted = isIgnoringBatteryOptimizations()
        if (hasNotificationAccess) {
            NotificationListenerService.requestRebind(
                ComponentName(this, SpendingNotificationListenerService::class.java)
            )
        }
    }

    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 41)
        }
    }

    private fun isNotificationListenerEnabled(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return enabled.split(":").any { it.contains(packageName, ignoreCase = true) }
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val pm = getSystemService(PowerManager::class.java) ?: return true
        return pm.isIgnoringBatteryOptimizations(packageName)
    }

    @SuppressLint("BatteryLife")
    private fun requestBatteryUnrestricted() {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        } catch (_: Exception) {
            startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }

    private fun shareExportFile(file: java.io.File) {
        val uri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            file
        )
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Expense export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(share, "Bagikan export"))
    }
}
