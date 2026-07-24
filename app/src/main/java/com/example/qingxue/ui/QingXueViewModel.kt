package com.example.qingxue.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.qingxue.data.CountdownEventEntity
import com.example.qingxue.data.DailyMatchEntity
import com.example.qingxue.data.DemoReview
import com.example.qingxue.data.DailyQuoteEntity
import com.example.qingxue.data.FocusOutcome
import com.example.qingxue.data.AiAnalysisEntity
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.StudyRepository
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.data.TaskFocusTotal
import com.example.qingxue.focus.FocusTimerState
import com.example.qingxue.focus.FocusTimerService
import com.example.qingxue.focus.FocusTimerStore
import com.example.qingxue.focus.PendingFocusSettlement
import com.example.qingxue.rating.FormRatingCalculator
import com.example.qingxue.rating.FormRatingSummary
import com.example.qingxue.util.daysUntil
import com.example.qingxue.util.nextStudyDayBoundary
import com.example.qingxue.util.recentDateStrings
import com.example.qingxue.util.todayString
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

data class DayStat(
    val date: String,
    val focusMinutes: Int,
    val completedTasks: Int,
    val totalTasks: Int
)

data class CountdownItem(
    val event: CountdownEventEntity,
    val daysRemaining: Long
)

data class HabitStat(
    val task: StudyTaskEntity,
    val totalMinutes: Long
)

data class StudyHistoryState(
    val tasks: List<StudyTaskEntity> = emptyList(),
    val sessions: List<FocusSessionEntity> = emptyList()
)

private data class TaskDashboardData(
    val tasks: List<StudyTaskEntity>,
    val habitTotals: List<TaskFocusTotal>
)

private data class DateWindowData(
    val today: String,
    val recentDates: List<String>,
    val tasks: List<StudyTaskEntity>,
    val sessions: List<FocusSessionEntity>,
    val dailyQuote: DailyQuoteEntity?,
    val dailyMatch: DailyMatchEntity?
)

data class DashboardState(
    val todayTasks: List<StudyTaskEntity> = emptyList(),
    val todayFocusMinutes: Int = 0,
    val todaySessions: List<FocusSessionEntity> = emptyList(),
    val dailyMatch: DailyMatchEntity? = null,
    val recentStats: List<DayStat> = emptyList(),
    val countdowns: List<CountdownItem> = emptyList(),
    val habitStats: List<HabitStat> = emptyList(),
    val dailyQuote: DailyQuoteEntity? = null,
    val formRating: FormRatingSummary = FormRatingCalculator.calculate(
        recentDates = emptyList(),
        tasks = emptyList(),
        sessions = emptyList()
    )
) {
    val completedToday: Int get() = todayTasks.count { it.completed }
    val totalToday: Int get() = todayTasks.size
    val todayProgress: Float get() = if (totalToday == 0) 0f else completedToday.toFloat() / totalToday
    val streakDays: Int
        get() {
            var streak = 0
            for (stat in recentStats.asReversed()) {
                if (stat.focusMinutes > 0 || stat.completedTasks > 0) streak++ else break
            }
            return streak
        }
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class QingXueViewModel(
    private val repository: StudyRepository,
    private val focusTimerStore: FocusTimerStore,
    application: Application
) : ViewModel() {
    private val applicationContext = application.applicationContext
    private val currentDate = MutableStateFlow(todayString())
    private val _focusTimerState = MutableStateFlow(FocusTimerState())
    val focusTimerState: StateFlow<FocusTimerState> = _focusTimerState.asStateFlow()
    private val _pendingFocusSettlement = MutableStateFlow<PendingFocusSettlement?>(null)
    val pendingFocusSettlement: StateFlow<PendingFocusSettlement?> =
        _pendingFocusSettlement.asStateFlow()
    private val _aiAnalysis = MutableStateFlow<AiAnalysisEntity?>(null)
    val aiAnalysis: StateFlow<AiAnalysisEntity?> = _aiAnalysis.asStateFlow()
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()
    private val _backupMessage = MutableStateFlow<String?>(null)
    val backupMessage: StateFlow<String?> = _backupMessage.asStateFlow()
    private val taskDashboardData = currentDate.flatMapLatest { date ->
        combine(
            repository.tasksForDate(date),
            repository.habitFocusTotals()
        ) { tasks, habitTotals ->
            TaskDashboardData(tasks, habitTotals)
        }
    }
    private val dateWindowData = currentDate.flatMapLatest { date ->
        val recentDates = recentDateStrings(today = LocalDate.parse(date))
        combine(
            repository.tasksBetween(recentDates.first(), recentDates.last()),
            repository.sessionsBetween(recentDates.first(), recentDates.last()),
            repository.quoteForDate(date),
            repository.dailyMatch(date)
        ) { tasks, sessions, dailyQuote, dailyMatch ->
            DateWindowData(date, recentDates, tasks, sessions, dailyQuote, dailyMatch)
        }
    }

    val historyState: StateFlow<StudyHistoryState> = combine(
        repository.allTasks(),
        repository.allSessions()
    ) { tasks, sessions ->
        StudyHistoryState(tasks = tasks, sessions = sessions)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StudyHistoryState())

    init {
        viewModelScope.launch {
            repository.refreshDailyQuote(currentDate.value)
        }
        viewModelScope.launch {
            while (true) {
                val now = ZonedDateTime.now()
                val nextBoundary = nextStudyDayBoundary(now)
                val waitMillis = Duration.between(now, nextBoundary).toMillis()
                    .coerceAtLeast(1_000L) + 500L
                delay(waitMillis)
                val newDate = todayString()
                if (newDate != currentDate.value) {
                    currentDate.value = newDate
                    repository.refreshDailyQuote(newDate)
                }
            }
        }
        viewModelScope.launch {
            _aiAnalysis.value = repository.latestAiAnalysis()
        }
        viewModelScope.launch {
            val persistedState = focusTimerStore.currentTimerState()
            if (persistedState.hasStarted) {
                FocusTimerService.restore(applicationContext)
            }
        }
        viewModelScope.launch {
            focusTimerStore.timerState.collect { state ->
                _focusTimerState.value = state
            }
        }
        viewModelScope.launch {
            focusTimerStore.pendingSettlement.collect { settlement ->
                _pendingFocusSettlement.value = settlement
            }
        }
    }

    val dashboardState: StateFlow<DashboardState> = combine(
        taskDashboardData,
        dateWindowData,
        repository.countdownEvents()
    ) { taskData, window, countdownEvents ->
        val todayHabitSeconds = window.sessions
            .filter { it.date == window.today && it.habitId != null }
            .groupBy { checkNotNull(it.habitId) }
            .mapValues { (_, sessions) -> sessions.sumOf { it.effectiveSeconds() } }
        val todayTasks = taskData.tasks.map { task ->
            if (task.isHabit) {
                task.copy(
                    completed = (todayHabitSeconds[task.id] ?: 0) >= task.estimatedMinutes * 60
                )
            } else {
                task
            }
        }
        val stats = window.recentDates.map { date ->
            val tasks = window.tasks.filter { it.date == date }
            val minutes = window.sessions.filter { it.date == date }.sumOf { it.durationMinutes }
            DayStat(
                date = date,
                focusMinutes = minutes,
                completedTasks = tasks.count { it.completed },
                totalTasks = tasks.size
            )
        }
        val countdowns = countdownEvents
            .map { event ->
                CountdownItem(
                    event,
                    daysUntil(event.targetDate, LocalDate.parse(window.today))
                )
            }
            .sortedWith(
                compareBy<CountdownItem> { !it.event.isPinned }
                    .thenBy { it.daysRemaining < 0 }
                    .thenBy { item ->
                        if (item.daysRemaining >= 0) item.daysRemaining else -item.daysRemaining
                    }
                    .thenBy { it.event.createdAt }
            )
        val totalMinutesByTask = taskData.habitTotals.associate { it.taskId to it.totalMinutes }
        val habitStats = todayTasks
            .filter { it.isHabit }
            .map { task -> HabitStat(task, totalMinutesByTask[task.id] ?: 0L) }
            .sortedWith(
                compareByDescending<HabitStat> { it.totalMinutes }
                    .thenBy { it.task.createdAt }
            )
        val ratingTasks = (window.tasks + todayTasks.filter { it.isHabit }).distinctBy { it.id }
        DashboardState(
            todayTasks = todayTasks,
            todayFocusMinutes = window.sessions.todayMinutes(window.today),
            todaySessions = window.sessions.filter { it.date == window.today },
            dailyMatch = window.dailyMatch,
            recentStats = stats,
            countdowns = countdowns,
            habitStats = habitStats,
            dailyQuote = window.dailyQuote,
            formRating = FormRatingCalculator.calculate(
                recentDates = window.recentDates,
                tasks = ratingTasks,
                sessions = window.sessions
            )
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())
    fun setDailyMatch(mainTaskId: Long?, manualObjective: String, plannedRounds: Int) {
        if (mainTaskId == null && manualObjective.isBlank()) return
        viewModelScope.launch {
            repository.setDailyMatch(
                date = currentDate.value,
                mainTaskId = mainTaskId,
                manualObjective = manualObjective,
                plannedRounds = plannedRounds
            )
        }
    }
    fun addTask(
        title: String,
        subject: String,
        description: String,
        minutes: Int,
        isHabit: Boolean,
        studyType: String,
        isCore: Boolean,
        habitId: Long?
    ) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.addTask(
                title = title,
                subject = subject,
                description = description,
                estimatedMinutes = minutes,
                date = currentDate.value,
                isHabit = isHabit,
                studyType = studyType,
                isCore = isCore,
                habitId = habitId
            )
        }
    }

    fun toggleTask(task: StudyTaskEntity) {
        if (task.isHabit) return
        viewModelScope.launch {
            repository.setTaskCompleted(task, !task.completed, currentDate.value)
        }
    }

    fun deleteTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun restoreTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            repository.restoreTask(task)
        }
    }

    fun updateTask(task: StudyTaskEntity) {
        if (task.title.isBlank()) return
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun addCountdownEvent(
        title: String,
        targetDate: String,
        description: String,
        isPinned: Boolean
    ) {
        if (title.isBlank()) return
        val validDate = runCatching { daysUntil(targetDate) }.getOrNull() ?: return
        if (validDate < 0) return
        viewModelScope.launch {
            repository.addCountdownEvent(title, targetDate, description, isPinned)
        }
    }

    fun toggleCountdownPinned(event: CountdownEventEntity) {
        viewModelScope.launch {
            repository.setCountdownPinned(event, !event.isPinned)
        }
    }

    fun deleteCountdownEvent(event: CountdownEventEntity) {
        viewModelScope.launch {
            repository.deleteCountdownEvent(event)
        }
    }

    fun updateCountdownEvent(event: CountdownEventEntity) {
        if (event.title.isBlank()) return
        runCatching { daysUntil(event.targetDate) }.getOrNull() ?: return
        viewModelScope.launch {
            repository.updateCountdownEvent(event)
        }
    }

    fun setPomodoroConfig(focusMinutes: Int, breakMinutes: Int, cycles: Int) {
        val current = _focusTimerState.value
        if (current.hasStarted) return
        viewModelScope.launch {
            focusTimerStore.saveTimerState(
                FocusTimerState(
                focusMinutes = focusMinutes.coerceIn(1, 120),
                breakMinutes = breakMinutes.coerceIn(1, 60),
                totalCycles = cycles.coerceIn(1, 8),
                pausedRemainingSeconds = focusMinutes.coerceIn(1, 120) * 60
                )
            )
        }
    }

    fun startFocusTimer(
        selectedId: Long?,
        winCondition: String,
        focusMinutes: Int,
        breakMinutes: Int,
        cycles: Int
    ) {
        val current = _focusTimerState.value
        if (current.isRunning) return
        val configured = if (current.hasStarted) {
            current
        } else {
            FocusTimerState(
                focusMinutes = focusMinutes.coerceIn(1, 120),
                breakMinutes = breakMinutes.coerceIn(1, 60),
                totalCycles = cycles.coerceIn(1, 8),
                pausedRemainingSeconds = focusMinutes.coerceIn(1, 120) * 60
            )
        }
        val selected = historyState.value.tasks.firstOrNull { it.id == selectedId }
            ?: dashboardState.value.todayTasks.firstOrNull { it.id == selectedId }
        val taskId = selected?.id?.takeUnless { selected.isHabit }
        val habitId = if (selected?.isHabit == true) selected.id else selected?.habitId
        viewModelScope.launch {
            if (!current.hasStarted) focusTimerStore.saveTimerState(configured)
            FocusTimerService.startOrResume(
                context = applicationContext,
                state = configured,
                taskId = taskId,
                habitId = habitId,
                taskTitle = selected?.title,
                winCondition = winCondition.trim().take(160)
            )
        }
    }

    fun reconcileFocusTimer() {
        if (_focusTimerState.value.hasPhaseElapsed()) {
            FocusTimerService.restore(applicationContext)
        }
    }
    fun pauseFocusTimer() {
        if (_focusTimerState.value.isRunning) FocusTimerService.pause(applicationContext)
    }

    fun endFocusTimer() {
        if (_focusTimerState.value.hasStarted) FocusTimerService.end(applicationContext)
    }

    fun settleFocusSession(review: DemoReview) {
        val pending = _pendingFocusSettlement.value ?: return
        viewModelScope.launch {
            repository.settleFocusSession(pending.sessionId, review)
            focusTimerStore.clearPendingSettlement()
        }
    }

    fun skipFocusSettlement(reflection: String) {
        val pending = _pendingFocusSettlement.value
        viewModelScope.launch {
            if (pending != null && reflection.isNotBlank()) {
                repository.updateReflection(pending.sessionId, reflection)
            }
            focusTimerStore.clearPendingSettlement()
        }
    }

    fun saveReflection(sessionId: Long, reflection: String) {
        viewModelScope.launch {
            repository.updateReflection(sessionId, reflection)
        }
    }

    fun addManualFocus(
        selectedTaskId: Long?,
        startedAt: Long,
        durationMinutes: Int,
        reflection: String
    ) {
        val selectedTask = selectedTaskId?.let { id ->
            historyState.value.tasks.firstOrNull { it.id == id }
        }
        viewModelScope.launch {
            repository.recordManualFocus(
                selectedTask = selectedTask,
                startedAt = startedAt,
                durationMinutes = durationMinutes,
                reflection = reflection
            )
        }
    }

    fun deleteFocusSession(sessionId: Long) {
        viewModelScope.launch {
            repository.discardFocusSession(sessionId)
        }
    }

    fun discardFocusSession() {
        val pending = _pendingFocusSettlement.value ?: return
        viewModelScope.launch {
            repository.discardFocusSession(pending.sessionId)
            focusTimerStore.clearPendingSettlement()
        }
    }

    fun exportBackup(uri: Uri, themeAccent: String) {
        viewModelScope.launch {
            try {
                val json = repository.exportBackup(themeAccent)
                withContext(Dispatchers.IO) {
                    val stream = applicationContext.contentResolver.openOutputStream(uri)
                        ?: error("无法打开备份文件")
                    stream.bufferedWriter(Charsets.UTF_8).use { it.write(json) }
                }
                _backupMessage.value = "备份已导出"
            } catch (error: Exception) {
                _backupMessage.value = "导出失败：${error.message ?: "未知错误"}"
            }
        }
    }

    fun importBackup(uri: Uri, onThemeRestored: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    val stream = applicationContext.contentResolver.openInputStream(uri)
                        ?: error("无法读取备份文件")
                    stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                }
                val restoredTheme = repository.importBackup(json)
                FocusTimerService.stop(applicationContext)
                focusTimerStore.saveTimerState(FocusTimerState())
                focusTimerStore.clearPendingSettlement()
                _aiAnalysis.value = repository.latestAiAnalysis()
                restoredTheme?.let(onThemeRestored)
                _backupMessage.value = "数据已恢复"
            } catch (error: Exception) {
                _backupMessage.value = "恢复失败：${error.message ?: "备份文件无效"}"
            }
        }
    }

    fun clearBackupMessage() {
        _backupMessage.value = null
    }

    fun clearFocusData() {
        viewModelScope.launch {
            repository.clearAllFocusData()
            _aiAnalysis.value = null
        }
    }

    fun loadCachedAnalysis() {
        viewModelScope.launch {
            _aiAnalysis.value = repository.latestAiAnalysis()
        }
    }

    fun requestAiAnalysis() {
        if (_isAnalyzing.value) return
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisError.value = null
            try {
                val state = dashboardState.value
                val history = historyState.value
                val date = currentDate.value
                val recentDates = recentDateStrings(today = LocalDate.parse(date))
                val entity = repository.requestAiAnalysis(
                    date = date,
                    periodStart = recentDates.first(),
                    periodEnd = recentDates.last(),
                    summary = state.formRating,
                    tasks = history.tasks,
                    sessions = history.sessions,
                    context = applicationContext
                )
                _aiAnalysis.value = entity
            } catch (e: Exception) {
                _analysisError.value = e.message ?: "未知错误"
            }
            _isAnalyzing.value = false
        }
    }

    private fun List<FocusSessionEntity>.todayMinutes(date: String): Int {
        return filter { it.date == date }.sumOf { it.effectiveSeconds() } / 60
    }

    private fun FocusSessionEntity.effectiveSeconds(): Int =
        actualSeconds.takeIf { it > 0 } ?: durationMinutes * 60
}

class QingXueViewModelFactory(
    private val repository: StudyRepository,
    private val focusTimerStore: FocusTimerStore,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QingXueViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QingXueViewModel(repository, focusTimerStore, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
