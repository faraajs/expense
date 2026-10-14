package com.example.autospending.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomCategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CustomCategoryEntity)

    @Delete
    suspend fun delete(category: CustomCategoryEntity)

    @Query("SELECT * FROM custom_categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CustomCategoryEntity>>

    @Query("SELECT * FROM custom_categories ORDER BY name ASC")
    suspend fun getAll(): List<CustomCategoryEntity>
}
