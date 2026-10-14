package com.example.autospending.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_expenses")
data class RecurringExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Long,
    val category: String = "Bills",
    /** MONTHLY | WEEKLY | YEARLY */
    val frequency: String = "MONTHLY",
    /** Day of month (1-28) for MONTHLY, day of week (1=Mon) for WEEKLY */
    val dayOfPeriod: Int = 1,
    val isActive: Boolean = true,
    val nextDueMillis: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
