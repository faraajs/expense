package com.example.autospending.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SpendingRepository(
    private val dao: SpendingDao,
    private val rawLogDao: RawLogDao? = null,
) {
    private val insertMutex = Mutex()

    fun observeTransactionsForMonth(month: MonthWindow): Flow<List<TransactionEntity>> =
        dao.observeTransactionsForRange(month.startMillis, month.endMillis)

    fun observeTransactionsForRange(startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>> =
        dao.observeTransactionsForRange(startMillis, endMillis)

    fun observeRecentTransactions(): Flow<List<TransactionEntity>> =
        dao.observeRecentTransactions()

    fun observeBudget(monthKey: String): Flow<BudgetEntity?> =
        dao.observeBudget(monthKey)

    /**
     * Mutex-guarded dedup insert (SHA-256 contentHash unique index + count check).
     * Prevents races when NLS backfill and live post race on the same notification.
     */
    suspend fun saveParsedTransaction(transaction: TransactionEntity): Boolean =
        insertMutex.withLock {
            if (dao.countByContentHash(transaction.contentHash) > 0) return false
            return dao.insertTransaction(transaction) > 0
        }

    suspend fun addManualTransaction(transaction: TransactionEntity) {
        dao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }

    suspend fun setBudget(month: String, limitAmount: Long) {
        val current = dao.getBudget(month)
        dao.upsertBudget(
            current?.copy(limitAmount = limitAmount, notifiedThresholds = "")
                ?: BudgetEntity(month = month, limitAmount = limitAmount)
        )
    }

    suspend fun getBudget(month: String): BudgetEntity? = dao.getBudget(month)

    suspend fun markThresholdNotified(month: String, threshold: Int) {
        val budget = dao.getBudget(month) ?: return
        dao.upsertBudget(budget.withNotified(threshold))
    }

    suspend fun sumExpenses(month: MonthWindow): Long =
        dao.sumExpensesForRange(month.startMillis, month.endMillis)

    suspend fun getAllTransactions(): List<TransactionEntity> = dao.getAllTransactions()

    suspend fun countAllTransactions(): Int = dao.countAllTransactions()

    suspend fun exportTransactionsJson(appVersionName: String = ""): String {
        val all = dao.getAllTransactions()
        return TransactionExport.toJson(all, appVersionName = appVersionName)
    }

    suspend fun insertRawLog(entry: RawLogEntity) {
        rawLogDao?.insert(entry)
    }

    fun observeRawLog(limit: Int = 200): Flow<List<RawLogEntity>> =
        rawLogDao?.observeRecent(limit) ?: kotlinx.coroutines.flow.flowOf(emptyList())

    /**
     * Import exported JSON; skips rows whose contentHash already exists.
     */
    suspend fun importTransactionsJson(json: String): TransactionImport.Result {
        val rows = TransactionImport.parseTransactions(json)
        var imported = 0
        var skipped = 0
        var errors = 0
        for (row in rows) {
            try {
                val saved = saveParsedTransaction(row)
                if (saved) imported++ else skipped++
            } catch (_: Exception) {
                errors++
            }
        }
        return TransactionImport.Result(imported = imported, skipped = skipped, errors = errors)
    }
}
