package com.example.autospending.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecurringExpenseEntity): Long

    @Update
    suspend fun update(item: RecurringExpenseEntity)

    @Delete
    suspend fun delete(item: RecurringExpenseEntity)

    @Query("DELETE FROM recurring_expenses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM recurring_expenses ORDER BY name ASC")
    fun observeAll(): Flow<List<RecurringExpenseEntity>>

    @Query("SELECT * FROM recurring_expenses WHERE isActive = 1 AND nextDueMillis <= :nowMillis")
    suspend fun getDueItems(nowMillis: Long = System.currentTimeMillis()): List<RecurringExpenseEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM recurring_expenses WHERE isActive = 1 AND frequency = 'MONTHLY'")
    fun observeMonthlyTotal(): Flow<Long>
}
