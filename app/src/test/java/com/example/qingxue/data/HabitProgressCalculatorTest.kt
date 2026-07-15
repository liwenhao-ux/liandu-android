package com.example.qingxue.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitProgressCalculatorTest {
    private val today = LocalDate.of(2026, 7, 15)

    @Test
    fun linkedTasksAndDirectFocusAreSummedOnce() {
        val sessions = listOf(
            session(taskId = 11, habitId = 1, seconds = 22 * 60),
            session(taskId = 12, habitId = 1, seconds = 38 * 60),
            session(taskId = 13, habitId = 1, seconds = 12 * 60),
            session(taskId = null, habitId = 1, seconds = 5 * 60),
            session(taskId = 20, habitId = null, seconds = 40 * 60),
            session(taskId = 21, habitId = 2, seconds = 60 * 60)
        )

        val progress = HabitProgressCalculator.calculate(
            sessions = sessions,
            habitId = 1,
            dailyTargetMinutes = 60,
            today = today
        )

        assertEquals(77 * 60, progress.todaySeconds)
        assertEquals(77 * 60, progress.totalSeconds)
        assertTrue(progress.completedToday)
    }

    @Test
    fun legacyMinuteDurationIsUsedWhenActualSecondsIsMissing() {
        val progress = HabitProgressCalculator.calculate(
            sessions = listOf(
                session(taskId = 11, habitId = 1, seconds = 0, durationMinutes = 30)
            ),
            habitId = 1,
            dailyTargetMinutes = 30,
            today = today
        )

        assertEquals(30 * 60, progress.todaySeconds)
        assertTrue(progress.completedToday)
    }

    @Test
    fun unfinishedTodayKeepsStreakEndingYesterday() {
        val sessions = listOf(
            session(habitId = 1, seconds = 10 * 60),
            session(habitId = 1, seconds = 60 * 60, date = today.minusDays(1)),
            session(habitId = 1, seconds = 65 * 60, date = today.minusDays(2))
        )

        val progress = HabitProgressCalculator.calculate(
            sessions = sessions,
            habitId = 1,
            dailyTargetMinutes = 60,
            today = today
        )

        assertFalse(progress.completedToday)
        assertEquals(2, progress.streakDays)
    }

    private fun session(
        taskId: Long? = null,
        habitId: Long?,
        seconds: Int,
        durationMinutes: Int = seconds / 60,
        date: LocalDate = today
    ) = FocusSessionEntity(
        taskId = taskId,
        habitId = habitId,
        startedAt = 0L,
        endedAt = seconds * 1_000L,
        durationMinutes = durationMinutes,
        date = date.toString(),
        actualSeconds = seconds
    )
}