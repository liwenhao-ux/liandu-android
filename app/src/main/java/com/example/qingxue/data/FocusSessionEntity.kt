package com.example.qingxue.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class FocusEndReason(val storageValue: String) {
    Completed("COMPLETED"),
    EndedEarly("ENDED_EARLY")
}

enum class FocusOutcome(
    val storageValue: String,
    val label: String,
    val completesTask: Boolean
) {
    Unreviewed("UNREVIEWED", "未结算", false),
    Mastered("MASTERED", "完成且掌握", true),
    Unstable("UNSTABLE", "完成但不稳", true),
    Partial("PARTIAL", "部分推进", false),
    NoProgress("NO_PROGRESS", "基本没推进", false);

    companion object {
        fun fromStorage(value: String): FocusOutcome {
            return entries.firstOrNull { it.storageValue == value } ?: Unreviewed
        }

        val reviewOptions: List<FocusOutcome>
            get() = entries.filterNot { it == Unreviewed }
    }
}

@Entity(
    tableName = "focus_sessions",
    indices = [Index(value = ["habitId"])]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?,
    val habitId: Long? = null,
    val startedAt: Long,
    val endedAt: Long,
    val durationMinutes: Int,
    val date: String,
    @ColumnInfo(defaultValue = "0") val plannedMinutes: Int = 0,
    @ColumnInfo(defaultValue = "0") val actualSeconds: Int = 0,
    @ColumnInfo(defaultValue = "0") val pauseCount: Int = 0,
    @ColumnInfo(defaultValue = "0") val pausedSeconds: Int = 0,
    @ColumnInfo(defaultValue = "COMPLETED")
    val endReason: String = FocusEndReason.Completed.storageValue,
    @ColumnInfo(defaultValue = "UNREVIEWED")
    val outcome: String = FocusOutcome.Unreviewed.storageValue,
    @ColumnInfo(defaultValue = "0") val focusBlockMinutes: Int = 0,
    @ColumnInfo(defaultValue = "0") val breakMinutes: Int = 0,
    @ColumnInfo(defaultValue = "1") val plannedCycles: Int = 1,
    @ColumnInfo(defaultValue = "0") val completedCycles: Int = 0,
    @ColumnInfo(defaultValue = "") val reflection: String = ""
)
