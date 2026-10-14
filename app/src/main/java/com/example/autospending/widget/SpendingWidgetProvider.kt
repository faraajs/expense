package com.example.autospending.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.autospending.AutoSpendingApp
import com.example.autospending.MainActivity
import com.example.autospending.R
import com.example.autospending.data.formatIdr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * 2×2 home screen widget showing today's expense/income totals.
 * Tapping the widget opens the main app.
 */
class SpendingWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                android.content.ComponentName(context, SpendingWidgetProvider::class.java)
            )
            ids.forEach { updateWidget(context, manager, it) }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.example.autospending.widget.REFRESH"

        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val repo = (context.applicationContext as AutoSpendingApp).repository
            val views = RemoteViews(context.packageName, R.layout.widget_spending)

            // Tap to open app
            val launchIntent = Intent(context, MainActivity::class.java)
            val pendingLaunch = PendingIntent.getActivity(
                context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingLaunch)

            // Compute today's range
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startMs = cal.timeInMillis
            val endMs = startMs + 24 * 60 * 60 * 1000L

            CoroutineScope(Dispatchers.IO).launch {
                val txs = repo.observeTransactionsForRange(startMs, endMs).first()
                val expense = txs.filter { it.type == "expense" }.sumOf { it.amount }
                val income = txs.filter { it.type == "income" }.sumOf { it.amount }

                views.setTextViewText(R.id.widget_expense, "−${formatIdr(expense)}")
                views.setTextViewText(R.id.widget_income, "+${formatIdr(income)}")
                views.setTextViewText(R.id.widget_label, "Hari ini")

                manager.updateAppWidget(widgetId, views)
            }
        }
    }
}
