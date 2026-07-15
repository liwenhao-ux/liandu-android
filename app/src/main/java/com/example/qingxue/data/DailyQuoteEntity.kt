package com.example.qingxue.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_quotes")
data class DailyQuoteEntity(
    @PrimaryKey val date: String,
    val text: String,
    val source: String,
    val fetchedAt: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false,
    val networkAttempted: Boolean = false
)
