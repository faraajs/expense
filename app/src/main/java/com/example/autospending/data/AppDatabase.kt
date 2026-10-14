package com.example.autospending.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Room database for AutoSpending.
 *
 * Version history:
 * - v1 (on-device legacy): various identity hashes depending on build; core tables transactions (+ often budgets)
 * - v2: explicit multi-entity schema (transactions, budgets, raw_log, recurring_expenses, custom_categories)
 *
 * CRITICAL: Never use fallbackToDestructiveMigration on the release path — user data lives here.
 */
@Database(
    entities = [
        TransactionEntity::class,
        BudgetEntity::class,
        RawLogEntity::class,
        RecurringExpenseEntity::class,
        CustomCategoryEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spendingDao(): SpendingDao
    abstract fun rawLogDao(): RawLogDao
    abstract fun recurringExpenseDao(): RecurringExpenseDao
    abstract fun customCategoryDao(): CustomCategoryDao

    companion object {
        const val DB_NAME = "auto-spending.db"
        private const val TAG = "AppDatabase"

        /**
         * Upgrade any v1 on-disk DB to v2 without dropping user tables.
         * Uses IF NOT EXISTS so partial/legacy schemas survive identity-hash mismatches
         * (Room runs this when version increases from 1 → 2).
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.i(TAG, "Running MIGRATION_1_2 (preserve existing data)")

                // Core tables — create only if a very old build lacked them
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `transactions` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `type` TEXT NOT NULL,
                        `amount` INTEGER NOT NULL,
                        `currency` TEXT NOT NULL,
                        `occurredAt` INTEGER NOT NULL,
                        `sourcePackage` TEXT NOT NULL,
                        `sourceTitle` TEXT NOT NULL,
                        `rawText` TEXT NOT NULL,
                        `merchant` TEXT,
                        `category` TEXT NOT NULL,
                        `confidence` REAL NOT NULL,
                        `contentHash` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_transactions_contentHash` " +
                        "ON `transactions` (`contentHash`)"
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `budgets` (
                        `month` TEXT NOT NULL,
                        `limitAmount` INTEGER NOT NULL,
                        `alertThresholds` TEXT NOT NULL,
                        `notifiedThresholds` TEXT NOT NULL,
                        PRIMARY KEY(`month`)
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `raw_log` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `packageName` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `text` TEXT NOT NULL,
                        `bigText` TEXT NOT NULL,
                        `notificationKey` TEXT,
                        `postTime` INTEGER NOT NULL,
                        `receivedAt` INTEGER NOT NULL,
                        `whitelisted` INTEGER NOT NULL,
                        `outcome` TEXT NOT NULL,
                        `parseError` TEXT
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_raw_log_packageName_receivedAt` " +
                        "ON `raw_log` (`packageName`, `receivedAt`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_raw_log_outcome_receivedAt` " +
                        "ON `raw_log` (`outcome`, `receivedAt`)"
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `recurring_expenses` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `amount` INTEGER NOT NULL,
                        `category` TEXT NOT NULL,
                        `frequency` TEXT NOT NULL,
                        `dayOfPeriod` INTEGER NOT NULL,
                        `isActive` INTEGER NOT NULL,
                        `nextDueMillis` INTEGER NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `custom_categories` (
                        `name` TEXT NOT NULL,
                        `emoji` TEXT NOT NULL,
                        `keywords` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`name`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}
