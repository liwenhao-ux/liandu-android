package com.example.qingxue.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_matches")
data class DailyMatchEntity(
    @PrimaryKey val date: String,
    val mainTaskId: Long? = null,
    val manualObjective: String = "",
    val plannedRounds: Int = 2,
    val userNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)