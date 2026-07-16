package com.example.qingxue.ui

import com.example.qingxue.data.DailyMatchEntity
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.RoundResult
import com.example.qingxue.data.StudyTaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyMatchPresentationTest {
    @Test
    fun noSessions_startsInWarmUp() {
        val task = task(completed = false)
        val result = dailyMatchPresentation(
            match = DailyMatchEntity(date = "2026-07-16", mainTaskId = task.id),
            tasks = listOf(task),
            sessions = emptyList()
        )

        assertEquals("Warm-up", result.status)
        assertEquals("完成数据库迁移", result.objective)
        assertEquals(0, result.completedRounds)
    }

    @Test
    fun completedMainTask_isVictory() {
        val task = task(completed = true)
        val result = dailyMatchPresentation(
            match = DailyMatchEntity(date = "2026-07-16", mainTaskId = task.id),
            tasks = listOf(task),
            sessions = listOf(session(RoundResult.Win))
        )

        assertEquals("Victory", result.status)
        assertTrue(result.progress > 0f)
    }

    @Test
    fun manualObjective_ignoresUnrelatedWinningRound() {
        val result = dailyMatchPresentation(
            match = DailyMatchEntity(
                date = "2026-07-16",
                manualObjective = "完成统计页"
            ),
            tasks = emptyList(),
            sessions = listOf(
                session(RoundResult.Win).copy(
                    taskId = null,
                    winCondition = "复习英语"
                )
            )
        )

        assertTrue(result.status != "Victory")
    }
    private fun task(completed: Boolean) = StudyTaskEntity(
        id = 7,
        title = "完成数据库迁移",
        subject = "Kotlin",
        estimatedMinutes = 25,
        date = "2026-07-16",
        completed = completed
    )

    private fun session(result: RoundResult) = FocusSessionEntity(
        id = 1,
        taskId = 7,
        startedAt = 1_000,
        endedAt = 1_501_000,
        durationMinutes = 25,
        actualSeconds = 1_500,
        date = "2026-07-16",
        roundResult = result.storageValue
    )
}
