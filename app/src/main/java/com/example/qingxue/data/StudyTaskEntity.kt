package com.example.qingxue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class StudyTaskType(val storageValue: String, val label: String) {
    General("GENERAL", "普通任务"),
    Lecture("LECTURE", "讲义 / 看课"),
    Practice("PRACTICE", "章节刷题"),
    MockExam("MOCK_EXAM", "真题 / 模拟卷");

    companion object {
        fun fromStorage(value: String): StudyTaskType {
            return entries.firstOrNull { it.storageValue == value } ?: General
        }
    }
}

@Entity(
    tableName = "study_tasks",
    indices = [Index(value = ["habitId"])]
)
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    @ColumnInfo(defaultValue = "''") val description: String = "",
    val estimatedMinutes: Int,
    val date: String,
    val completed: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isHabit: Boolean = false,
    val lastCompletedDate: String? = null,
    @ColumnInfo(defaultValue = "GENERAL") val studyType: String = StudyTaskType.General.storageValue,
    @ColumnInfo(defaultValue = "0") val isCore: Boolean = false,
    val habitId: Long? = null,
    @ColumnInfo(defaultValue = "0") val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
