package com.example.qingxue.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    @Query("SELECT * FROM study_tasks ORDER BY createdAt DESC")
    fun allTasks(): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks ORDER BY createdAt DESC")
    suspend fun allTasksOnce(): List<StudyTaskEntity>

    @Query("SELECT * FROM study_tasks WHERE isArchived = 0 AND (date = :date OR isHabit = 1)")
    fun tasksForDate(date: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM study_tasks WHERE isArchived = 0 AND isHabit = 0 AND date BETWEEN :startDate AND :endDate")
    fun tasksBetween(startDate: String, endDate: String): Flow<List<StudyTaskEntity>>

    @Query("SELECT * FROM focus_sessions WHERE date BETWEEN :startDate AND :endDate")
    fun sessionsBetween(startDate: String, endDate: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun allSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    suspend fun allSessionsOnce(): List<FocusSessionEntity>

    @Query("SELECT * FROM countdown_events ORDER BY targetDate ASC, createdAt ASC")
    fun countdownEvents(): Flow<List<CountdownEventEntity>>

    @Query("SELECT * FROM countdown_events ORDER BY targetDate ASC, createdAt ASC")
    suspend fun allCountdownEventsOnce(): List<CountdownEventEntity>

    @Query("SELECT * FROM daily_quotes ORDER BY date ASC")
    suspend fun allDailyQuotesOnce(): List<DailyQuoteEntity>

    @Query("SELECT * FROM ai_analyses ORDER BY createdAt DESC")
    suspend fun allAnalysesOnce(): List<AiAnalysisEntity>

    @Query(
        """
        SELECT study_tasks.id AS taskId,
               COALESCE(SUM(
                   CASE WHEN focus_sessions.actualSeconds > 0
                        THEN focus_sessions.actualSeconds
                        ELSE focus_sessions.durationMinutes * 60
                   END
               ), 0) / 60 AS totalMinutes
        FROM study_tasks
        LEFT JOIN focus_sessions ON focus_sessions.habitId = study_tasks.id
        WHERE study_tasks.isHabit = 1 AND study_tasks.isArchived = 0
        GROUP BY study_tasks.id
        """
    )
    fun habitFocusTotals(): Flow<List<TaskFocusTotal>>

    @Query("SELECT * FROM daily_quotes WHERE date = :date LIMIT 1")
    fun quoteForDate(date: String): Flow<DailyQuoteEntity?>

    @Query("SELECT * FROM daily_quotes WHERE date = :date LIMIT 1")
    suspend fun quoteForDateOnce(date: String): DailyQuoteEntity?

    @Query("SELECT * FROM study_tasks WHERE id = :id LIMIT 1")
    suspend fun taskById(id: Long): StudyTaskEntity?

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun sessionById(id: Long): FocusSessionEntity?

    @Query("SELECT text FROM daily_quotes WHERE date < :date ORDER BY date DESC LIMIT 30")
    suspend fun recentQuoteTexts(date: String): List<String>

    @Insert
    suspend fun insertTask(task: StudyTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<StudyTaskEntity>)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Delete
    suspend fun deleteTask(task: StudyTaskEntity)

    @Query("UPDATE study_tasks SET habitId = NULL WHERE habitId = :habitId")
    suspend fun clearHabitFromTasks(habitId: Long)

    @Insert
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<FocusSessionEntity>)

    @Delete
    suspend fun deleteSession(session: FocusSessionEntity)

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Insert
    suspend fun insertCountdownEvent(event: CountdownEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCountdownEvents(events: List<CountdownEventEntity>)

    @Delete
    suspend fun deleteCountdownEvent(event: CountdownEventEntity)

    @Update
    suspend fun updateCountdownEvent(event: CountdownEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyQuote(quote: DailyQuoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuotes(quotes: List<DailyQuoteEntity>)

    @Query("SELECT * FROM ai_analyses ORDER BY createdAt DESC LIMIT 1")
    suspend fun latestAnalysis(): AiAnalysisEntity?

    @Insert
    suspend fun insertAnalysis(analysis: AiAnalysisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnalyses(analyses: List<AiAnalysisEntity>)

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM study_tasks")
    suspend fun deleteAllTasks()

    @Query("DELETE FROM countdown_events")
    suspend fun deleteAllCountdownEvents()

    @Query("DELETE FROM daily_quotes")
    suspend fun deleteAllDailyQuotes()

    @Query("DELETE FROM ai_analyses")
    suspend fun deleteAllAnalyses()

    @Query("UPDATE focus_sessions SET reflection = :reflection WHERE id = :sessionId")
    suspend fun updateSessionReflection(sessionId: Long, reflection: String)

    @Transaction
    suspend fun replaceAllData(backup: AppBackupData) {
        deleteAllSessions()
        deleteAllTasks()
        deleteAllCountdownEvents()
        deleteAllDailyQuotes()
        deleteAllAnalyses()
        insertTasks(backup.tasks)
        insertSessions(backup.sessions)
        insertCountdownEvents(backup.countdownEvents)
        insertDailyQuotes(backup.dailyQuotes)
        insertAnalyses(backup.aiAnalyses)
    }
}
