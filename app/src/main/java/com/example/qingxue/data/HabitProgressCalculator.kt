package com.example.qingxue.data

import java.time.LocalDate

data class HabitProgress(
    val todaySeconds: Int,
    val totalSeconds: Int,
    val streakDays: Int,
    val completedToday: Boolean
)

object HabitProgressCalculator {
    fun calculate(
        sessions: List<FocusSessionEntity>,
        habitId: Long,
        dailyTargetMinutes: Int,
        today: LocalDate = LocalDate.now()
    ): HabitProgress {
        val habitSessions = sessions.filter { it.habitId == habitId }
        val secondsByDate = habitSessions
            .groupBy { it.date }
            .mapValues { (_, daySessions) ->
                daySessions.sumOf { it.effectiveSeconds() }
            }
        val targetSeconds = dailyTargetMinutes.coerceAtLeast(1) * 60
        val todaySeconds = secondsByDate[today.toString()] ?: 0

        var cursor = today
        if (todaySeconds < targetSeconds) cursor = cursor.minusDays(1)
        var streak = 0
        while ((secondsByDate[cursor.toString()] ?: 0) >= targetSeconds) {
            streak++
            cursor = cursor.minusDays(1)
        }

        return HabitProgress(
            todaySeconds = todaySeconds,
            totalSeconds = habitSessions.sumOf { it.effectiveSeconds() },
            streakDays = streak,
            completedToday = todaySeconds >= targetSeconds
        )
    }

    private fun FocusSessionEntity.effectiveSeconds(): Int =
        actualSeconds.takeIf { it > 0 } ?: durationMinutes * 60
}