package com.example.qingxue.focus

enum class PomodoroPhase(val label: String) {
    Focus("专注"),
    Break("休息")
}

data class FocusTimerState(
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val totalCycles: Int = 2,
    val phase: PomodoroPhase = PomodoroPhase.Focus,
    val currentCycle: Int = 1,
    val completedCycles: Int = 0,
    val completedFocusSeconds: Int = 0,
    val isRunning: Boolean = false,
    val pausedRemainingSeconds: Int = 25 * 60,
    val startedAt: Long = 0L,
    val endsAt: Long = 0L,
    val activeTaskId: Long? = null,
    val activeHabitId: Long? = null,
    val activeTaskTitle: String? = null,
    val winCondition: String = "",
    val pauseCount: Int = 0,
    val pausedSeconds: Int = 0,
    val pausedAt: Long = 0L
) {
    val phaseTotalSeconds: Int
        get() = when (phase) {
            PomodoroPhase.Focus -> focusMinutes * 60
            PomodoroPhase.Break -> breakMinutes * 60
        }
    val plannedFocusSeconds: Int get() = focusMinutes * totalCycles * 60
    val hasStarted: Boolean get() = startedAt > 0L

    fun remainingSeconds(nowMillis: Long = System.currentTimeMillis()): Int {
        if (!isRunning) return pausedRemainingSeconds
        val millisRemaining = (endsAt - nowMillis).coerceAtLeast(0L)
        return ((millisRemaining + 999L) / 1_000L)
            .toInt()
            .coerceIn(0, phaseTotalSeconds)
    }

    fun hasPhaseElapsed(nowMillis: Long = System.currentTimeMillis()): Boolean =
        isRunning && endsAt > 0L && nowMillis >= endsAt

    fun actualFocusSeconds(nowMillis: Long = System.currentTimeMillis()): Int {
        val currentBlockSeconds = if (hasStarted && phase == PomodoroPhase.Focus) {
            phaseTotalSeconds - remainingSeconds(nowMillis)
        } else {
            0
        }
        return (completedFocusSeconds + currentBlockSeconds)
            .coerceIn(0, plannedFocusSeconds)
    }

    fun resetForNextPlan(): FocusTimerState {
        return FocusTimerState(
            focusMinutes = focusMinutes,
            breakMinutes = breakMinutes,
            totalCycles = totalCycles,
            pausedRemainingSeconds = focusMinutes * 60
        )
    }
}

data class PendingFocusSettlement(
    val sessionId: Long,
    val taskTitle: String?,
    val winCondition: String,
    val actualMinutes: Int,
    val completedTimer: Boolean
)
