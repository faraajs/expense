package com.example.autospending.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["contentHash"], unique = true)]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val amount: Long,
    val currency: String = "IDR",
    val occurredAt: Long,
    val sourcePackage: String,
    val sourceTitle: String,
    val rawText: String,
    val merchant: String?,
    val category: String,
    val confidence: Float,
    val contentHash: String,
    val createdAt: Long = System.currentTimeMillis()
)
