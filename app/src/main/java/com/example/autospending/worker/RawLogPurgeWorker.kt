package com.example.autospending.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.autospending.AutoSpendingApp
import java.util.concurrent.TimeUnit

/**
 * Deletes raw_log rows older than 21 days.
 * Scheduled daily via WorkManager from [schedule].
 */
class RawLogPurgeWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val app = applicationContext as AutoSpendingApp
            val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(RETENTION_DAYS)
            val deleted = app.database.rawLogDao().deleteOlderThan(cutoff)
            Log.i(TAG, "Purged $deleted raw_log rows older than $RETENTION_DAYS days")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Purge failed: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "RawLogPurgeWorker"
        const val UNIQUE_NAME = "raw_log_purge_daily"
        const val RETENTION_DAYS = 21L

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<RawLogPurgeWorker>(1, TimeUnit.DAYS).build()
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
                UNIQUE_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
