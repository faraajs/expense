package com.example.autospending.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpendingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT * FROM transactions
        WHERE occurredAt >= :startMillis AND occurredAt < :endMillis
        ORDER BY occurredAt DESC
        """
    )
    fun observeTransactionsForRange(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecentTransactions(limit: Int = 100): Flow<List<TransactionEntity>>

    @Query("SELECT COUNT(*) FROM transactions WHERE contentHash = :contentHash")
    suspend fun countByContentHash(contentHash: String): Int

    @Query("SELECT * FROM transactions ORDER BY occurredAt DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun countAllTransactions(): Int

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0) FROM transactions
        WHERE type = 'expense' AND occurredAt >= :startMillis AND occurredAt < :endMillis
        """
    )
    suspend fun sumExpensesForRange(startMillis: Long, endMillis: Long): Long

    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    fun observeBudget(month: String): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    suspend fun getBudget(month: String): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBudget(budget: BudgetEntity)
}
