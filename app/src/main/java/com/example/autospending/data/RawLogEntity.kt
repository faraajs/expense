package com.example.autospending.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Raw notification log — Phase 0 diagnostic.
 * Every whitelisted notification (and optionally all packages if diagnostic_log_all=true)
 * is written here before parser runs. Rows older than 21 days are auto-purged by RawLogPurgeWorker.
 */
@Entity(
    tableName = "raw_log",
    indices = [
        Index(value = ["packageName", "receivedAt"]),
        Index(value = ["outcome", "receivedAt"])
    ]
)
data class RawLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val title: String,
    val text: String,
    val bigText: String = "",
    val notificationKey: String? = null,
    val postTime: Long,
    val receivedAt: Long = System.currentTimeMillis(),
    val whitelisted: Boolean,
    /** PARSED | SKIPPED_NON_TX | PARSE_FAIL | CAPTURED */
    val outcome: String = "CAPTURED",
    val parseError: String? = null
)
