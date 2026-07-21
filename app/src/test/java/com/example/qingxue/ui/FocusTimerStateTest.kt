package com.example.qingxue.ui

import com.example.qingxue.focus.FocusTimerState
import com.example.qingxue.focus.PomodoroPhase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusTimerStateTest {
    @Test
    fun defaultsToTwoRoundPomodoro() {
        val state = FocusTimerState()

        assertEquals(25, state.focusMinutes)
        assertEquals(5, state.breakMinutes)
        assertEquals(2, state.totalCycles)
        assertEquals(25 * 60, state.phaseTotalSeconds)
        assertEquals(50 * 60, state.plannedFocusSeconds)
    }

    @Test
    fun runningFocusCountsOnlyElapsedBlockTime() {
        val state = FocusTimerState(
            focusMinutes = 1,
            totalCycles = 2,
            isRunning = true,
            startedAt = 1L,
            endsAt = 60_001L,
            pausedRemainingSeconds = 60
        )

        assertEquals(30, state.actualFocusSeconds(nowMillis = 30_001L))
    }

    @Test
    fun pausedFocusKeepsElapsedTime() {
        val state = FocusTimerState(
            focusMinutes = 1,
            isRunning = false,
            startedAt = 1L,
            pausedRemainingSeconds = 20
        )

        assertEquals(40, state.actualFocusSeconds(nowMillis = 50_000L))
    }

    @Test
    fun breakTimeIsExcludedFromActualFocus() {
        val state = FocusTimerState(
            focusMinutes = 25,
            breakMinutes = 5,
            phase = PomodoroPhase.Break,
            currentCycle = 1,
            completedCycles = 1,
            completedFocusSeconds = 25 * 60,
            isRunning = true,
            startedAt = 1L,
            endsAt = 300_001L,
            pausedRemainingSeconds = 5 * 60
        )

        assertEquals(25 * 60, state.actualFocusSeconds(nowMillis = 120_001L))
    }

    @Test
    fun runningPhaseReportsElapsedAtItsDeadline() {
        val state = FocusTimerState(isRunning = true, startedAt = 1L, endsAt = 10_000L)

        assertFalse(state.hasPhaseElapsed(nowMillis = 9_999L))
        assertTrue(state.hasPhaseElapsed(nowMillis = 10_000L))
    }

    @Test
    fun pausedPhaseNeverRequestsReconciliation() {
        val state = FocusTimerState(isRunning = false, startedAt = 1L, endsAt = 10_000L)

        assertFalse(state.hasPhaseElapsed(nowMillis = 20_000L))
    }
}
