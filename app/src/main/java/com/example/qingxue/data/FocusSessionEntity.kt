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

enum class RoundResult(val storageValue: String, val label: String) {
    Unreviewed("UNREVIEWED", "未复盘"),
    Win("WIN", "胜利"),
    PartialWin("PARTIAL_WIN", "部分胜利"),
    Loss("LOSS", "失利");

    companion object {
        fun fromStorage(value: String): RoundResult =
            entries.firstOrNull { it.storageValue == value } ?: Unreviewed
    }
}

enum class FocusQuality(val storageValue: String, val label: String) {
    Unreviewed("UNREVIEWED", "未评价"),
    Excellent("EXCELLENT", "极佳"),
    Good("GOOD", "良好"),
    Unstable("UNSTABLE", "不稳定"),
    Poor("POOR", "较差");

    companion object {
        fun fromStorage(value: String): FocusQuality =
            entries.firstOrNull { it.storageValue == value } ?: Unreviewed
    }
}

data class DemoReview(
    val result: RoundResult,
    val focusQuality: FocusQuality,
    val wentWell: String,
    val problemDescription: String,
    val nextCall: String,
    val distractionCount: Int
)
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
    @ColumnInfo(defaultValue = "") val reflection: String = "",
    @ColumnInfo(defaultValue = "") val winCondition: String = "",
    @ColumnInfo(defaultValue = "UNREVIEWED")
    val roundResult: String = RoundResult.Unreviewed.storageValue,
    @ColumnInfo(defaultValue = "UNREVIEWED")
    val focusQuality: String = FocusQuality.Unreviewed.storageValue,
    @ColumnInfo(defaultValue = "") val wentWell: String = "",
    @ColumnInfo(defaultValue = "") val problemDescription: String = "",
    @ColumnInfo(defaultValue = "") val nextCall: String = "",
    @ColumnInfo(defaultValue = "0") val distractionCount: Int = 0
)
