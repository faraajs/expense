package com.example.autospending.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val month: String,
    val limitAmount: Long,
    val alertThresholds: String = "80,100",
    val notifiedThresholds: String = ""
) {
    fun alertThresholdValues(): List<Int> =
        alertThresholds.split(",").mapNotNull { it.trim().toIntOrNull() }.sorted()

    fun notifiedThresholdValues(): Set<Int> =
        notifiedThresholds.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    fun withNotified(threshold: Int): BudgetEntity {
        val updated = (notifiedThresholdValues() + threshold).sorted().joinToString(",")
        return copy(notifiedThresholds = updated)
    }
}
