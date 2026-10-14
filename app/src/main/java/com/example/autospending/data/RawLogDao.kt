package com.example.autospending.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RawLogDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: RawLogEntity): Long

    @Query("SELECT * FROM raw_log ORDER BY receivedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 200): Flow<List<RawLogEntity>>

    @Query("SELECT * FROM raw_log ORDER BY receivedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int = 200): List<RawLogEntity>

    @Query("DELETE FROM raw_log WHERE receivedAt < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long): Int

    @Query("SELECT COUNT(*) FROM raw_log")
    suspend fun count(): Int
}
