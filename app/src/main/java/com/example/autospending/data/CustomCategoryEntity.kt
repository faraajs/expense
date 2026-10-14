package com.example.autospending.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-defined spending category.
 * Built-in categories (Food, Transport, etc.) are code-defined and not stored here.
 * Custom categories are additive — they appear alongside built-ins.
 */
@Entity(tableName = "custom_categories")
data class CustomCategoryEntity(
    @PrimaryKey val name: String,
    val emoji: String = "💳",
    /** Space-separated keyword list for auto-matching during parse. */
    val keywords: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun keywordList(): List<String> = keywords.split(" ").map { it.trim().lowercase() }.filter { it.isNotBlank() }
}
