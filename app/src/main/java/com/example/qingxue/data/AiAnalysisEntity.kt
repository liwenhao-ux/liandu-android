package com.example.qingxue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_analyses")
data class AiAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    @ColumnInfo(defaultValue = "") val periodStart: String = "",
    @ColumnInfo(defaultValue = "") val periodEnd: String = "",
    val overallComment: String,
    val dimensionAnalysis: String,
    val advice: String,
    val createdAt: Long = System.currentTimeMillis()
)
