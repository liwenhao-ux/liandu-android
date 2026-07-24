package com.example.qingxue.data

import java.time.LocalDateTime
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManualFocusSessionFactoryTest {
    private val zone = ZoneId.of("Asia/Shanghai")

    @Test
    fun taskSessionKeepsHabitSnapshotAndManualSource() {
        val task = StudyTaskEntity(
            id = 7,
            title = "英语阅读",
            subject = "英语",
            estimatedMinutes = 45,
            date = "2026-07-24",
            habitId = 5
        )
        val start = millis("2026-07-24T09:00")
        val session = ManualFocusSessionFactory.create(
            draft = ManualFocusDraft(task, start, 45, "  完成一篇阅读  "),
            nowMillis = millis("2026-07-24T10:00"),
            zoneId = zone
        )

        assertEquals(7L, session.taskId)
        assertEquals(5L, session.habitId)
        assertEquals(2_700, session.actualSeconds)
        assertEquals("完成一篇阅读", session.reflection)
        assertTrue(session.isManual)
    }

    @Test
    fun habitSessionUsesHabitIdWithoutCreatingTaskLink() {
        val habit = StudyTaskEntity(
            id = 5,
            title = "学习英语",
            subject = "英语",
            estimatedMinutes = 60,
            date = "2026-07-24",
            isHabit = true
        )
        val session = ManualFocusSessionFactory.create(
            draft = ManualFocusDraft(habit, millis("2026-07-24T08:00"), 30),
            nowMillis = millis("2026-07-24T09:00"),
            zoneId = zone
        )

        assertNull(session.taskId)
        assertEquals(5L, session.habitId)
    }

    @Test
    fun beforeFourAmBelongsToPreviousStudyDay() {
        val session = ManualFocusSessionFactory.create(
            draft = ManualFocusDraft(null, millis("2026-07-24T03:30"), 20),
            nowMillis = millis("2026-07-24T05:00"),
            zoneId = zone
        )

        assertEquals("2026-07-23", session.date)
    }

    @Test
    fun futureEndTimeIsRejected() {
        val result = runCatching {
            ManualFocusSessionFactory.create(
                draft = ManualFocusDraft(null, millis("2026-07-24T09:50"), 20),
                nowMillis = millis("2026-07-24T10:00"),
                zoneId = zone
            )
        }

        assertTrue(result.isFailure)
    }

    @Test
    fun overlapDetectionFindsIntersectingSessionButNotAdjacentOne() {
        val existing = FocusSessionEntity(
            id = 12,
            taskId = null,
            startedAt = millis("2026-07-24T09:00"),
            endedAt = millis("2026-07-24T09:30"),
            durationMinutes = 30,
            date = "2026-07-24"
        )

        assertEquals(
            existing,
            findOverlappingFocusSession(
                sessions = listOf(existing),
                startedAt = millis("2026-07-24T09:20"),
                durationMinutes = 20
            )
        )
        assertNull(
            findOverlappingFocusSession(
                sessions = listOf(existing),
                startedAt = millis("2026-07-24T09:30"),
                durationMinutes = 20
            )
        )
    }

    @Test
    fun overlapDetectionExcludesSessionBeingEdited() {
        val existing = FocusSessionEntity(
            id = 12,
            taskId = null,
            startedAt = millis("2026-07-24T09:00"),
            endedAt = millis("2026-07-24T09:30"),
            durationMinutes = 30,
            date = "2026-07-24"
        )

        assertNull(
            findOverlappingFocusSession(
                sessions = listOf(existing),
                startedAt = millis("2026-07-24T09:05"),
                durationMinutes = 20,
                excludedSessionId = existing.id
            )
        )
    }

    private fun millis(value: String): Long =
        LocalDateTime.parse(value).atZone(zone).toInstant().toEpochMilli()
}