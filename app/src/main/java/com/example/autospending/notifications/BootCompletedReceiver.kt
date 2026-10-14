package com.example.autospending.notifications

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.util.Log

/**
 * After reboot / app update, nudge the notification listener to rebind.
 * Samsung often leaves listeners dead until the user re-opens settings or the app.
 */
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action.orEmpty()
        Log.i(TAG, "Received $action — requesting NLS rebind")
        val component = ComponentName(context, SpendingNotificationListenerService::class.java)
        NotificationListenerService.requestRebind(component)
    }

    companion object {
        private const val TAG = "AutoSpendingBoot"
    }
}
