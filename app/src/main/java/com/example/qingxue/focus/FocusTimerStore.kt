package com.example.qingxue.focus

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.focusTimerDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "focus_timer_state"
)

class FocusTimerStore(context: Context) {
    private val dataStore = context.applicationContext.focusTimerDataStore

    val timerState: Flow<FocusTimerState> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map(::readTimerState)

    val pendingSettlement: Flow<PendingFocusSettlement?> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map(::readPendingSettlement)

    suspend fun currentTimerState(): FocusTimerState = timerState.first()

    suspend fun saveTimerState(state: FocusTimerState) {
        dataStore.edit { preferences ->
            preferences[FOCUS_MINUTES] = state.focusMinutes
            preferences[BREAK_MINUTES] = state.breakMinutes
            preferences[TOTAL_CYCLES] = state.totalCycles
            preferences[PHASE] = state.phase.name
            preferences[CURRENT_CYCLE] = state.currentCycle
            preferences[COMPLETED_CYCLES] = state.completedCycles
            preferences[COMPLETED_FOCUS_SECONDS] = state.completedFocusSeconds
            preferences[IS_RUNNING] = state.isRunning
            preferences[PAUSED_REMAINING_SECONDS] = state.pausedRemainingSeconds
            preferences[STARTED_AT] = state.startedAt
            preferences[ENDS_AT] = state.endsAt
            preferences[PAUSE_COUNT] = state.pauseCount
            preferences[PAUSED_SECONDS] = state.pausedSeconds
            preferences[PAUSED_AT] = state.pausedAt
            state.activeTaskId?.let { preferences[ACTIVE_TASK_ID] = it }
                ?: preferences.remove(ACTIVE_TASK_ID)
            state.activeHabitId?.let { preferences[ACTIVE_HABIT_ID] = it }
                ?: preferences.remove(ACTIVE_HABIT_ID)
            state.activeTaskTitle?.let { preferences[ACTIVE_TASK_TITLE] = it }
                ?: preferences.remove(ACTIVE_TASK_TITLE)
        }
    }

    suspend fun savePendingSettlement(settlement: PendingFocusSettlement) {
        dataStore.edit { preferences ->
            preferences[PENDING_SESSION_ID] = settlement.sessionId
            preferences[PENDING_ACTUAL_MINUTES] = settlement.actualMinutes
            preferences[PENDING_COMPLETED] = settlement.completedTimer
            settlement.taskTitle?.let { preferences[PENDING_TASK_TITLE] = it }
                ?: preferences.remove(PENDING_TASK_TITLE)
        }
    }

    suspend fun clearPendingSettlement() {
        dataStore.edit { preferences ->
            preferences.remove(PENDING_SESSION_ID)
            preferences.remove(PENDING_TASK_TITLE)
            preferences.remove(PENDING_ACTUAL_MINUTES)
            preferences.remove(PENDING_COMPLETED)
        }
    }

    private fun readTimerState(preferences: Preferences): FocusTimerState {
        val focusMinutes = preferences[FOCUS_MINUTES] ?: 25
        return FocusTimerState(
            focusMinutes = focusMinutes,
            breakMinutes = preferences[BREAK_MINUTES] ?: 5,
            totalCycles = preferences[TOTAL_CYCLES] ?: 2,
            phase = runCatching {
                PomodoroPhase.valueOf(preferences[PHASE] ?: PomodoroPhase.Focus.name)
            }.getOrDefault(PomodoroPhase.Focus),
            currentCycle = preferences[CURRENT_CYCLE] ?: 1,
            completedCycles = preferences[COMPLETED_CYCLES] ?: 0,
            completedFocusSeconds = preferences[COMPLETED_FOCUS_SECONDS] ?: 0,
            isRunning = preferences[IS_RUNNING] ?: false,
            pausedRemainingSeconds = preferences[PAUSED_REMAINING_SECONDS]
                ?: focusMinutes * 60,
            startedAt = preferences[STARTED_AT] ?: 0L,
            endsAt = preferences[ENDS_AT] ?: 0L,
            activeTaskId = preferences[ACTIVE_TASK_ID],
            activeHabitId = preferences[ACTIVE_HABIT_ID],
            activeTaskTitle = preferences[ACTIVE_TASK_TITLE],
            pauseCount = preferences[PAUSE_COUNT] ?: 0,
            pausedSeconds = preferences[PAUSED_SECONDS] ?: 0,
            pausedAt = preferences[PAUSED_AT] ?: 0L
        )
    }

    private fun readPendingSettlement(preferences: Preferences): PendingFocusSettlement? {
        val sessionId = preferences[PENDING_SESSION_ID] ?: return null
        return PendingFocusSettlement(
            sessionId = sessionId,
            taskTitle = preferences[PENDING_TASK_TITLE],
            actualMinutes = preferences[PENDING_ACTUAL_MINUTES] ?: 0,
            completedTimer = preferences[PENDING_COMPLETED] ?: false
        )
    }

    private companion object {
        val FOCUS_MINUTES = intPreferencesKey("focus_minutes")
        val BREAK_MINUTES = intPreferencesKey("break_minutes")
        val TOTAL_CYCLES = intPreferencesKey("total_cycles")
        val PHASE = stringPreferencesKey("phase")
        val CURRENT_CYCLE = intPreferencesKey("current_cycle")
        val COMPLETED_CYCLES = intPreferencesKey("completed_cycles")
        val COMPLETED_FOCUS_SECONDS = intPreferencesKey("completed_focus_seconds")
        val IS_RUNNING = booleanPreferencesKey("is_running")
        val PAUSED_REMAINING_SECONDS = intPreferencesKey("paused_remaining_seconds")
        val STARTED_AT = longPreferencesKey("started_at")
        val ENDS_AT = longPreferencesKey("ends_at")
        val ACTIVE_TASK_ID = longPreferencesKey("active_task_id")
        val ACTIVE_HABIT_ID = longPreferencesKey("active_habit_id")
        val ACTIVE_TASK_TITLE = stringPreferencesKey("active_task_title")
        val PAUSE_COUNT = intPreferencesKey("pause_count")
        val PAUSED_SECONDS = intPreferencesKey("paused_seconds")
        val PAUSED_AT = longPreferencesKey("paused_at")
        val PENDING_SESSION_ID = longPreferencesKey("pending_session_id")
        val PENDING_TASK_TITLE = stringPreferencesKey("pending_task_title")
        val PENDING_ACTUAL_MINUTES = intPreferencesKey("pending_actual_minutes")
        val PENDING_COMPLETED = booleanPreferencesKey("pending_completed")
    }
}
