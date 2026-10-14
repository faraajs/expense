package com.example.autospending.notifications

import android.util.Log
import com.example.autospending.data.ListenerHealthTracker
import com.example.autospending.data.RawLogEntity
import com.example.autospending.data.SpendingRepository
import com.example.autospending.data.UserSettingsStore
import com.example.autospending.parser.NotificationPayload
import com.example.autospending.parser.PackageCatalog
import com.example.autospending.parser.SpendingParser

/**
 * Pure ingest pipeline used by [SpendingNotificationListenerService].
 * Writes raw_log, parses via live [SpendingParser], saves with dedup mutex.
 */
class NotificationIngestHandler(
    private val repository: SpendingRepository,
    private val settings: UserSettingsStore,
    private val health: ListenerHealthTracker,
    private val budgetAlertManager: BudgetAlertManager,
    private val parser: SpendingParser = SpendingParser(),
) {
    suspend fun handle(payload: NotificationPayload, fromBackfill: Boolean) {
        val tracked = PackageCatalog.isTracked(payload.packageName)
        val diagnosticAll = settings.diagnosticLogAll.value
        val parsersEnabled = settings.parsersEnabled.value
        val shouldLog = tracked || diagnosticAll

        if (shouldLog) {
            repository.insertRawLog(
                RawLogEntity(
                    packageName = payload.packageName,
                    title = payload.title.take(200),
                    text = payload.text.take(2000),
                    postTime = payload.postedAt,
                    whitelisted = tracked,
                    outcome = "CAPTURED",
                )
            )
        }

        if (!parsersEnabled) {
            if (tracked || diagnosticAll) {
                health.recordSkipped(payload.packageName)
            }
            return
        }

        val transaction = parser.parse(payload)
        if (transaction == null) {
            if (tracked) {
                health.recordFailed(payload.packageName)
                Log.w(
                    TAG,
                    "Unparsed tracked pkg=${payload.packageName}"
                )
            } else if (diagnosticAll) {
                health.recordSkipped(payload.packageName)
            }
            return
        }

        val saved = repository.saveParsedTransaction(transaction)
        if (tracked) health.recordParsed(payload.packageName)
        Log.i(
            TAG,
            "${if (fromBackfill) "Backfill" else "Live"} " +
                "pkg=${payload.packageName} saved=$saved"
        )
        if (saved && transaction.type == "expense") {
            budgetAlertManager.maybeNotifyBudgetCrossed(transaction.occurredAt)
        }
    }

    companion object {
        private const val TAG = "NotificationIngest"
    }
}
