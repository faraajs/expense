package com.example.autospending.notifications

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.autospending.AutoSpendingApp
import com.example.autospending.parser.NotificationPayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SpendingNotificationListenerService : NotificationListenerService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val app: AutoSpendingApp
        get() = application as AutoSpendingApp

    private val ingest: NotificationIngestHandler
        get() = app.notificationIngestHandler

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Notification listener created")
        app.budgetAlertManager.ensureChannel()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        app.listenerHealthTracker.onConnected()
        Log.i(TAG, "Notification listener connected — backfilling active notifications")
        try {
            activeNotifications?.forEach { processNotification(it, fromBackfill = true) }
        } catch (e: Exception) {
            Log.w(TAG, "Backfill failed: ${e.message}")
        }
    }

    override fun onListenerDisconnected() {
        Log.w(TAG, "Notification listener disconnected — requesting rebind")
        app.listenerHealthTracker.onDisconnected()
        requestRebind(
            android.content.ComponentName(this, SpendingNotificationListenerService::class.java)
        )
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        processNotification(sbn, fromBackfill = false)
    }

    private fun processNotification(sbn: StatusBarNotification, fromBackfill: Boolean) {
        if (sbn.packageName == packageName) return
        if ((sbn.notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return
        val payload = sbn.toPayload() ?: return
        serviceScope.launch {
            ingest.handle(payload, fromBackfill)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun StatusBarNotification.toPayload(): NotificationPayload? {
        val extras = notification.extras ?: return null
        val title = extras.charSeq(Notification.EXTRA_TITLE)
            .ifBlank { extras.charSeq(Notification.EXTRA_TITLE_BIG) }
        val textParts = buildList {
            add(extras.charSeq(Notification.EXTRA_TEXT))
            add(extras.charSeq(Notification.EXTRA_BIG_TEXT))
            add(extras.charSeq(Notification.EXTRA_SUB_TEXT))
            add(extras.charSeq(Notification.EXTRA_INFO_TEXT))
            add(extras.charSeq(Notification.EXTRA_SUMMARY_TEXT))
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
                ?.mapNotNull { it?.toString()?.trim()?.takeIf(String::isNotBlank) }
                ?.let { addAll(it) }
            notification.tickerText?.toString()?.trim()?.takeIf { it.isNotBlank() }?.let { add(it) }
        }
        val text = textParts
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" ")
        if (title.isBlank() && text.isBlank()) return null
        return NotificationPayload(
            packageName = this.packageName,
            title = title,
            text = text,
            postedAt = postTime.takeIf { it > 0 } ?: System.currentTimeMillis(),
        )
    }

    private fun Bundle.charSeq(key: String): String =
        getCharSequence(key)?.toString()?.trim().orEmpty()

    companion object {
        private const val TAG = "SpendingNLS"
    }
}
