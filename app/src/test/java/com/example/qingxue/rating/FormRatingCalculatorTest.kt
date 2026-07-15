package com.example.qingxue.rating

import com.example.qingxue.data.FocusEndReason
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.StudyTaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FormRatingCalculatorTest {
    private val dates = listOf(
        "2026-07-08",
        "2026-07-09",
        "2026-07-10",
        "2026-07-11",
        "2026-07-12",
        "2026-07-13",
        "2026-07-14"
    )

    @Test
    fun emptyDataStaysInCalibration() {
        val result = FormRatingCalculator.calculate(dates, emptyList(), emptyList())

        assertNull(result.rating)
        assertEquals(0, result.activeDays)
        assertEquals(0.5f, result.execution, 0.001f)
        assertEquals(0.5f, result.focus, 0.001f)
    }

    @Test
    fun habitWithoutDailyHistoryDoesNotCreatePhantomEvidence() {
        val habit = task(1, dates.last(), completed = false).copy(isHabit = true)

        val result = FormRatingCalculator.calculate(dates, listOf(habit), emptyList())

        assertEquals(0, result.evidenceCount)
        assertEquals(0, result.activeDays)
        assertNull(result.rating)
    }

    @Test
    fun ratingRequiresThreeActiveDaysAndFourEvidenceItems() {
        val twoDaySessions = listOf(
            session(1, dates[5], completed = true),
            session(2, dates[6], completed = true)
        )
        val tasks = listOf(
            task(1, dates[5], completed = true),
            task(2, dates[6], completed = true)
        )

        val calibrating = FormRatingCalculator.calculate(dates, tasks, twoDaySessions)
        val ready = FormRatingCalculator.calculate(
            dates,
            tasks,
            twoDaySessions + session(3, dates[4], completed = true)
        )

        assertNull(calibrating.rating)
        assertNotNull(ready.rating)
        assertEquals(3, ready.activeDays)
        assertTrue(ready.rating!! in 0.70..1.30)
    }

    @Test
    fun earlyEndingAndLongPausesLowerFocusScore() {
        val completed = FormRatingCalculator.calculate(
            dates,
            emptyList(),
            listOf(session(1, dates.last(), completed = true))
        )
        val interrupted = FormRatingCalculator.calculate(
            dates,
            emptyList(),
            listOf(
                session(
                    id = 2,
                    date = dates.last(),
                    completed = false,
                    actualSeconds = 10 * 60,
                    pausedSeconds = 5 * 60
                )
            )
        )

        assertTrue(completed.focus > interrupted.focus)
        assertEquals(1f, completed.focus, 0.001f)
    }

    @Test
    fun completedCoreTaskWithBoundFocusHasFullImpact() {
        val coreTask = task(7, dates.last(), completed = true, isCore = true)
        val result = FormRatingCalculator.calculate(
            dates,
            listOf(coreTask),
            listOf(session(1, dates.last(), completed = true, taskId = coreTask.id))
        )

        assertEquals(1f, result.impact, 0.001f)
    }

    private fun task(
        id: Long,
        date: String,
        completed: Boolean,
        isCore: Boolean = false
    ): StudyTaskEntity {
        return StudyTaskEntity(
            id = id,
            title = "任务$id",
            subject = "数学",
            estimatedMinutes = 25,
            date = date,
            completed = completed,
            isCore = isCore
        )
    }

    private fun session(
        id: Long,
        date: String,
        completed: Boolean,
        actualSeconds: Int = 25 * 60,
        pausedSeconds: Int = 0,
        taskId: Long? = null
    ): FocusSessionEntity {
        return FocusSessionEntity(
            id = id,
            taskId = taskId,
            startedAt = 0,
            endedAt = actualSeconds * 1_000L,
            durationMinutes = actualSeconds / 60,
            date = date,
            plannedMinutes = 25,
            actualSeconds = actualSeconds,
            pausedSeconds = pausedSeconds,
            endReason = if (completed) {
                FocusEndReason.Completed.storageValue
            } else {
                FocusEndReason.EndedEarly.storageValue
            }
        )
    }
}
