package com.example.autospending.notifications

import android.annotation.SuppressLint
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.autospending.R
import com.example.autospending.data.SpendingRepository
import com.example.autospending.data.formatIdr
import com.example.autospending.data.monthWindow
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

class BudgetAlertManager(
    private val context: Context,
    private val repository: SpendingRepository
) {
    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.budget_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Monthly spending budget alerts"
        }
        manager.createNotificationChannel(channel)
    }

    suspend fun maybeNotifyBudgetCrossed(occurredAt: Long) {
        val month = YearMonth.from(Instant.ofEpochMilli(occurredAt).atZone(ZoneId.systemDefault()))
        val window = monthWindow(month)
        val budget = repository.getBudget(window.key) ?: return
        if (budget.limitAmount <= 0) return

        val total = repository.sumExpenses(window)
        val notified = budget.notifiedThresholdValues()
        val crossed = budget.alertThresholdValues().firstOrNull { threshold ->
            threshold !in notified && total >= budget.limitAmount * threshold / 100
        } ?: return

        showBudgetNotification(crossed, total, budget.limitAmount)
        repository.markThresholdNotified(window.key, crossed)
    }

    @SuppressLint("MissingPermission")
    private fun showBudgetNotification(threshold: Int, total: Long, limit: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Budget $threshold% reached")
            .setContentText("${formatIdr(total)} used from ${formatIdr(limit)} this month")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(BUDGET_NOTIFICATION_ID + threshold, notification)
    }

    companion object {
        private const val CHANNEL_ID = "budget-alerts"
        private const val BUDGET_NOTIFICATION_ID = 3100
    }
}
