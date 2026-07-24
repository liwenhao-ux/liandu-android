package com.example.qingxue.data

import com.example.qingxue.util.studyDateStringAt
import java.time.ZoneId

data class ManualFocusDraft(
    val selectedTask: StudyTaskEntity?,
    val startedAt: Long,
    val durationMinutes: Int,
    val reflection: String = ""
)

object ManualFocusSessionFactory {
    fun create(
        draft: ManualFocusDraft,
        nowMillis: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): FocusSessionEntity {
        require(draft.durationMinutes in 1..960) { "专注时长需在 1 到 960 分钟之间" }
        require(draft.startedAt > 0L) { "开始时间无效" }

        val endedAt = draft.startedAt + draft.durationMinutes * 60_000L
        require(endedAt <= nowMillis) { "补记的结束时间不能晚于现在" }

        val selected = draft.selectedTask
        val taskId = selected?.id?.takeUnless { selected.isHabit }
        val habitId = if (selected?.isHabit == true) selected.id else selected?.habitId

        return FocusSessionEntity(
            taskId = taskId,
            habitId = habitId,
            startedAt = draft.startedAt,
            endedAt = endedAt,
            durationMinutes = draft.durationMinutes,
            date = studyDateStringAt(draft.startedAt, zoneId),
            plannedMinutes = draft.durationMinutes,
            actualSeconds = draft.durationMinutes * 60,
            endReason = FocusEndReason.Completed.storageValue,
            focusBlockMinutes = draft.durationMinutes.coerceAtMost(120),
            breakMinutes = 0,
            plannedCycles = 1,
            completedCycles = 1,
            reflection = draft.reflection.trim().take(500),
            isManual = true
        )
    }
}