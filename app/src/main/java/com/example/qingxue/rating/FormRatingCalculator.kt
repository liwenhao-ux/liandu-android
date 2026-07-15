package com.example.qingxue.rating

import com.example.qingxue.data.FocusEndReason
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.StudyTaskEntity
import kotlin.math.max

enum class RatingConfidence(val label: String) {
    Low("低"),
    Medium("中"),
    High("高")
}

data class FormRatingSummary(
    val rating: Double?,
    val execution: Float,
    val focus: Float,
    val consistency: Float,
    val impact: Float,
    val confidence: RatingConfidence,
    val activeDays: Int,
    val evidenceCount: Int
) {
    val isCalibrating: Boolean get() = rating == null
    val daysUntilReady: Int get() = (MIN_ACTIVE_DAYS - activeDays).coerceAtLeast(0)
    val evidenceUntilReady: Int get() = (MIN_EVIDENCE - evidenceCount).coerceAtLeast(0)
    val calibrationProgress: Float
        get() = minOf(
            activeDays.toFloat() / MIN_ACTIVE_DAYS,
            evidenceCount.toFloat() / MIN_EVIDENCE
        ).coerceIn(0f, 1f)

    companion object {
        const val MIN_ACTIVE_DAYS = 3
        const val MIN_EVIDENCE = 4
    }
}

object FormRatingCalculator {
    fun calculate(
        recentDates: List<String>,
        tasks: List<StudyTaskEntity>,
        sessions: List<FocusSessionEntity>
    ): FormRatingSummary {
        if (recentDates.isEmpty()) return emptySummary()

        val datedTasks = tasks.filter { !it.isHabit && it.date in recentDates }
        val relevantSessions = sessions.filter { it.date in recentDates }

        val execution = completionRatio(datedTasks)
        val focus = focusScore(relevantSessions)
        val activeDates = buildSet {
            relevantSessions.mapTo(this) { it.date }
            datedTasks.filter { it.completed }.mapTo(this) { it.date }
        }
        val firstEvidenceIndex = recentDates.indexOfFirst { date ->
            relevantSessions.any { it.date == date } || datedTasks.any { it.date == date }
        }
        val observedDays = if (firstEvidenceIndex == -1) 0 else recentDates.size - firstEvidenceIndex
        val consistency = if (observedDays == 0) {
            NEUTRAL_SCORE
        } else {
            (activeDates.size.toFloat() / observedDays).coerceIn(0f, 1f)
        }
        val impact = impactScore(tasks, relevantSessions)
        val activeDays = activeDates.size
        val evidenceCount = datedTasks.size + relevantSessions.size
        val isCalibrating = activeDays < FormRatingSummary.MIN_ACTIVE_DAYS ||
            evidenceCount < FormRatingSummary.MIN_EVIDENCE
        val overall = execution * 0.35f + focus * 0.25f + consistency * 0.25f + impact * 0.15f
        val rating = if (isCalibrating) null else RATING_MIN + overall * (RATING_MAX - RATING_MIN)

        return FormRatingSummary(
            rating = rating,
            execution = execution,
            focus = focus,
            consistency = consistency,
            impact = impact,
            confidence = confidence(activeDays, evidenceCount),
            activeDays = activeDays,
            evidenceCount = evidenceCount
        )
    }

    private fun completionRatio(tasks: List<StudyTaskEntity>): Float {
        if (tasks.isEmpty()) return NEUTRAL_SCORE
        return tasks.count { it.completed }.toFloat() / tasks.size
    }

    private fun focusScore(sessions: List<FocusSessionEntity>): Float {
        if (sessions.isEmpty()) return NEUTRAL_SCORE
        return sessions.map { session ->
            val actualSeconds = session.actualSeconds.takeIf { it > 0 }
                ?: session.durationMinutes * 60
            val plannedSeconds = max(
                session.plannedMinutes.takeIf { it > 0 }?.times(60) ?: actualSeconds,
                1
            )
            val completion = if (session.endReason == FocusEndReason.Completed.storageValue) {
                1f
            } else {
                (actualSeconds.toFloat() / plannedSeconds).coerceIn(0f, 1f)
            }
            val totalSessionSeconds = actualSeconds + session.pausedSeconds
            val pausedRatio = if (totalSessionSeconds == 0) {
                0f
            } else {
                session.pausedSeconds.toFloat() / totalSessionSeconds
            }
            val stability = (1f - pausedRatio.coerceIn(0f, 0.5f)).coerceIn(0f, 1f)
            completion * 0.8f + stability * 0.2f
        }.average().toFloat().coerceIn(0f, 1f)
    }

    private fun impactScore(
        tasks: List<StudyTaskEntity>,
        sessions: List<FocusSessionEntity>
    ): Float {
        val coreTasks = tasks.filter { it.isCore }
        if (coreTasks.isEmpty()) return NEUTRAL_SCORE

        val oneTimeCoreTasks = coreTasks.filterNot { it.isHabit }
        val completion = if (oneTimeCoreTasks.isEmpty()) {
            NEUTRAL_SCORE
        } else {
            oneTimeCoreTasks.count { it.completed }.toFloat() / oneTimeCoreTasks.size
        }
        val coreTaskIds = coreTasks.mapTo(mutableSetOf()) { it.id }
        val totalFocusSeconds = sessions.sumOf { session ->
            session.actualSeconds.takeIf { it > 0 } ?: session.durationMinutes * 60
        }
        val coreFocusSeconds = sessions
            .filter { session ->
                session.taskId?.let { it in coreTaskIds } == true ||
                    session.habitId?.let { it in coreTaskIds } == true
            }
            .sumOf { session ->
                session.actualSeconds.takeIf { it > 0 } ?: session.durationMinutes * 60
            }
        val coreFocusShare = if (totalFocusSeconds == 0) {
            0f
        } else {
            coreFocusSeconds.toFloat() / totalFocusSeconds
        }
        return (completion * 0.65f + coreFocusShare * 0.35f).coerceIn(0f, 1f)
    }

    private fun confidence(activeDays: Int, evidenceCount: Int): RatingConfidence {
        return when {
            activeDays >= 7 && evidenceCount >= 15 -> RatingConfidence.High
            activeDays >= 5 && evidenceCount >= 8 -> RatingConfidence.Medium
            else -> RatingConfidence.Low
        }
    }

    private fun emptySummary(): FormRatingSummary {
        return FormRatingSummary(
            rating = null,
            execution = NEUTRAL_SCORE,
            focus = NEUTRAL_SCORE,
            consistency = NEUTRAL_SCORE,
            impact = NEUTRAL_SCORE,
            confidence = RatingConfidence.Low,
            activeDays = 0,
            evidenceCount = 0
        )
    }

    private const val NEUTRAL_SCORE = 0.5f
    private const val RATING_MIN = 0.70
    private const val RATING_MAX = 1.30
}
