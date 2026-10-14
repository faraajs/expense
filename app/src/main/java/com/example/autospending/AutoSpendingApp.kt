package com.example.autospending

import android.app.Application
import com.example.autospending.data.AppDatabase
import com.example.autospending.data.ListenerHealthTracker
import com.example.autospending.data.SecureApiKeyStore
import com.example.autospending.data.SpendingRepository
import com.example.autospending.data.UserSettingsStore
import com.example.autospending.notifications.BudgetAlertManager
import com.example.autospending.notifications.NotificationIngestHandler
import com.example.autospending.worker.RawLogPurgeWorker

class AutoSpendingApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val repository: SpendingRepository by lazy {
        SpendingRepository(
            dao = database.spendingDao(),
            rawLogDao = database.rawLogDao(),
        )
    }
    val budgetAlertManager: BudgetAlertManager by lazy {
        BudgetAlertManager(this, repository)
    }
    val secureApiKeyStore: SecureApiKeyStore by lazy {
        SecureApiKeyStore(this)
    }
    val userSettingsStore: UserSettingsStore by lazy {
        UserSettingsStore(this)
    }
    val listenerHealthTracker: ListenerHealthTracker by lazy {
        ListenerHealthTracker(this)
    }
    val notificationIngestHandler: NotificationIngestHandler by lazy {
        NotificationIngestHandler(
            repository = repository,
            settings = userSettingsStore,
            health = listenerHealthTracker,
            budgetAlertManager = budgetAlertManager,
        )
    }

    override fun onCreate() {
        super.onCreate()
        secureApiKeyStore.getApiKey()
        RawLogPurgeWorker.schedule(this)
    }
}
