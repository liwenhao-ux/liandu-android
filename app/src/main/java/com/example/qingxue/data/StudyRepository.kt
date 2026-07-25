package com.example.qingxue.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class StudyRepository(private val dao: StudyDao) {
    fun allTasks(): Flow<List<StudyTaskEntity>> = dao.allTasks()

    fun tasksForDate(date: String): Flow<List<StudyTaskEntity>> {
        return dao.tasksForDate(date).map { tasks ->
            tasks
                .map { task ->
                    if (task.isHabit) {
                        task.copy(completed = task.lastCompletedDate == date)
                    } else {
                        task
                    }
                }
                .sortedWith(
                    compareBy<StudyTaskEntity> { it.completed }
                        .thenByDescending { it.isHabit }
                        .thenByDescending { it.createdAt }
                )
        }
    }

    fun tasksBetween(startDate: String, endDate: String): Flow<List<StudyTaskEntity>> {
        return dao.tasksBetween(startDate, endDate)
    }

    fun sessionsBetween(startDate: String, endDate: String): Flow<List<FocusSessionEntity>> {
        return dao.sessionsBetween(startDate, endDate)
    }

    fun allSessions(): Flow<List<FocusSessionEntity>> = dao.allSessions()

    fun countdownEvents(): Flow<List<CountdownEventEntity>> = dao.countdownEvents()

    fun habitFocusTotals(): Flow<List<TaskFocusTotal>> = dao.habitFocusTotals()

    fun quoteForDate(date: String): Flow<DailyQuoteEntity?> = dao.quoteForDate(date)
    fun dailyMatch(date: String): Flow<DailyMatchEntity?> = dao.dailyMatch(date)

    suspend fun setDailyMatch(
        date: String,
        mainTaskId: Long?,
        manualObjective: String,
        plannedRounds: Int
    ) {
        dao.upsertDailyMatch(
            DailyMatchEntity(
                date = date,
                mainTaskId = mainTaskId,
                manualObjective = manualObjective.trim().take(80),
                plannedRounds = plannedRounds.coerceIn(1, 8)
            )
        )
    }

    suspend fun addTask(
        title: String,
        subject: String,
        description: String,
        estimatedMinutes: Int,
        date: String,
        isHabit: Boolean,
        studyType: String,
        isCore: Boolean,
        habitId: Long?
    ) {
        dao.insertTask(
            StudyTaskEntity(
                title = title.trim(),
                subject = subject.trim().ifEmpty { "学习" },
                description = description.trim(),
                estimatedMinutes = estimatedMinutes.coerceIn(5, 240),
                date = date,
                isHabit = isHabit,
                studyType = StudyTaskType.fromStorage(studyType).storageValue,
                isCore = isCore,
                habitId = habitId.takeUnless { isHabit }
            )
        )
    }

    suspend fun setTaskCompleted(task: StudyTaskEntity, completed: Boolean, date: String) {
        val updated = if (task.isHabit) {
            task.copy(
                completed = completed,
                lastCompletedDate = if (completed) date else null
            )
        } else {
            task.copy(completed = completed)
        }
        dao.updateTask(updated)
    }

    suspend fun deleteTask(task: StudyTaskEntity) {
        if (task.isHabit) dao.clearHabitFromTasks(task.id)
        dao.updateTask(task.copy(isArchived = true))
    }

    suspend fun restoreTask(task: StudyTaskEntity) {
        dao.updateTask(task.copy(isArchived = false))
    }

    suspend fun updateTask(task: StudyTaskEntity) {
        val existing = dao.taskById(task.id)
        if (existing?.isHabit == true && !task.isHabit) {
            dao.clearHabitFromTasks(task.id)
        }
        dao.updateTask(
            task.copy(
                title = task.title.trim(),
                subject = task.subject.trim().ifEmpty { "学习" },
                description = task.description.trim(),
                estimatedMinutes = task.estimatedMinutes.coerceIn(5, 240),
                studyType = StudyTaskType.fromStorage(task.studyType).storageValue,
                habitId = task.habitId.takeUnless { task.isHabit }
            )
        )
    }

    suspend fun addCountdownEvent(
        title: String,
        targetDate: String,
        description: String,
        isPinned: Boolean
    ) {
        dao.insertCountdownEvent(
            CountdownEventEntity(
                title = title.trim(),
                targetDate = targetDate,
                description = description.trim(),
                isPinned = isPinned
            )
        )
    }

    suspend fun setCountdownPinned(event: CountdownEventEntity, isPinned: Boolean) {
        dao.updateCountdownEvent(event.copy(isPinned = isPinned))
    }

    suspend fun deleteCountdownEvent(event: CountdownEventEntity) {
        dao.deleteCountdownEvent(event)
    }

    suspend fun updateCountdownEvent(event: CountdownEventEntity) {
        dao.updateCountdownEvent(
            event.copy(
                title = event.title.trim(),
                description = event.description.trim()
            )
        )
    }

    suspend fun refreshDailyQuote(date: String) {
        val existing = dao.quoteForDateOnce(date)
        if (existing?.networkAttempted == true) return

        val recentQuotes = dao.recentQuoteTexts(date).toSet()
        val cached = existing ?: DailyQuoteEntity(
            date = date,
            text = fallbackQuote(date, recentQuotes),
            source = "LOCK IN"
        )

        // Mark before requesting so relaunching the app cannot retry repeatedly that day.
        dao.upsertDailyQuote(cached.copy(networkAttempted = true))

        val onlineQuote = fetchOnlineQuote() ?: return
        if (onlineQuote.text in recentQuotes) return

        dao.upsertDailyQuote(
            cached.copy(
                text = onlineQuote.text,
                source = onlineQuote.source,
                fetchedAt = System.currentTimeMillis(),
                isOnline = true,
                networkAttempted = true
            )
        )
    }

    suspend fun recordFocus(
        taskId: Long?,
        habitId: Long?,
        startedAt: Long,
        endedAt: Long,
        plannedMinutes: Int,
        actualSeconds: Int,
        pauseCount: Int,
        pausedSeconds: Int,
        endReason: FocusEndReason,
        focusBlockMinutes: Int,
        breakMinutes: Int,
        plannedCycles: Int,
        completedCycles: Int,
        winCondition: String,
        date: String
    ): Long {
        val durationMinutes = actualSeconds / 60
        if (durationMinutes <= 0) return 0L
        return dao.insertSession(
            FocusSessionEntity(
                taskId = taskId,
                habitId = habitId,
                startedAt = startedAt,
                endedAt = endedAt,
                durationMinutes = durationMinutes,
                date = date,
                plannedMinutes = plannedMinutes.coerceIn(1, 960),
                actualSeconds = actualSeconds.coerceAtLeast(0),
                pauseCount = pauseCount.coerceAtLeast(0),
                pausedSeconds = pausedSeconds.coerceAtLeast(0),
                endReason = endReason.storageValue,
                focusBlockMinutes = focusBlockMinutes.coerceIn(1, 120),
                breakMinutes = breakMinutes.coerceIn(1, 60),
                plannedCycles = plannedCycles.coerceIn(1, 8),
                completedCycles = completedCycles.coerceIn(0, plannedCycles.coerceIn(1, 8)),
                winCondition = winCondition.trim().take(160)
            )
        )
    }

    suspend fun saveManualFocus(
        sessionId: Long?,
        selectedTask: StudyTaskEntity?,
        startedAt: Long,
        durationMinutes: Int,
        reflection: String
    ): Long {
        val updated = ManualFocusSessionFactory.create(
            ManualFocusDraft(
                selectedTask = selectedTask,
                startedAt = startedAt,
                durationMinutes = durationMinutes,
                reflection = reflection
            )
        )
        if (sessionId == null) return dao.insertSession(updated)

        val existing = dao.sessionById(sessionId) ?: error("补记记录已不存在")
        require(existing.isManual) { "只有手动补记可以修改时间和归属" }
        dao.updateSession(updated.copy(id = existing.id))
        return existing.id
    }

    suspend fun updateReflection(sessionId: Long, reflection: String) {
        dao.updateSessionReflection(sessionId, reflection.trim())
    }

    suspend fun discardFocusSession(sessionId: Long) {
        val session = dao.sessionById(sessionId) ?: return
        dao.deleteSession(session)
    }

    suspend fun settleFocusSession(sessionId: Long, review: DemoReview) {
        val session = dao.sessionById(sessionId) ?: return
        val legacyOutcome = when (review.result) {
            RoundResult.Win -> FocusOutcome.Mastered
            RoundResult.PartialWin -> FocusOutcome.Partial
            RoundResult.Loss -> FocusOutcome.NoProgress
            RoundResult.Unreviewed -> FocusOutcome.Unreviewed
        }
        val reflection = listOf(
            review.wentWell.trim(),
            review.problemDescription.trim(),
            review.nextCall.trim()
        ).filter { it.isNotBlank() }.joinToString("\\n")
        dao.updateSession(
            session.copy(
                outcome = legacyOutcome.storageValue,
                roundResult = review.result.storageValue,
                focusQuality = review.focusQuality.storageValue,
                wentWell = review.wentWell.trim().take(500),
                problemDescription = review.problemDescription.trim().take(500),
                nextCall = review.nextCall.trim().take(300),
                distractionCount = review.distractionCount.coerceIn(0, 99),
                reflection = reflection.take(1200)
            )
        )
    }

    private suspend fun fetchOnlineQuote(): QuotePayload? = withContext(Dispatchers.IO) {
        val connection = (URL(QUOTE_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 4_000
            readTimeout = 4_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "LockIn/1.9.2 (Android)")
        }

        try {
            if (connection.responseCode !in 200..299) return@withContext null
            val body = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            val json = JSONObject(body)
            val text = json.optString("hitokoto").trim()
            if (text.length !in 2..48) return@withContext null
            QuotePayload(
                text = text,
                source = json.optString("from").trim().ifEmpty { "一言" }
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    private fun fallbackQuote(date: String, recentQuotes: Set<String>): String {
        val start = date.hashCode().let { if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it) } % FALLBACK_QUOTES.size
        return FALLBACK_QUOTES.indices
            .asSequence()
            .map { FALLBACK_QUOTES[(start + it) % FALLBACK_QUOTES.size] }
            .firstOrNull { it !in recentQuotes }
            ?: FALLBACK_QUOTES[start]
    }

    suspend fun exportBackup(themeAccent: String?): String = withContext(Dispatchers.IO) {
        AppBackupCodec.encode(
            AppBackupData(
                themeAccent = themeAccent,
                tasks = dao.allTasksOnce(),
                sessions = dao.allSessionsOnce(),
                dailyMatches = dao.allDailyMatchesOnce(),
                countdownEvents = dao.allCountdownEventsOnce(),
                dailyQuotes = dao.allDailyQuotesOnce(),
                aiAnalyses = dao.allAnalysesOnce()
            )
        )
    }

    fun previewBackup(json: String): AppBackupData = AppBackupCodec.decode(json)

    suspend fun importBackup(json: String): String? = withContext(Dispatchers.IO) {
        val backup = AppBackupCodec.decode(json)
        dao.replaceAllData(backup)
        backup.themeAccent
    }

    suspend fun clearAllFocusData() {
        dao.deleteAllSessions()
        dao.deleteAllAnalyses()
    }

    suspend fun latestAiAnalysis(): AiAnalysisEntity? {
        return dao.latestAnalysis()
    }

    suspend fun requestAiAnalysis(
        date: String,
        periodStart: String,
        periodEnd: String,
        summary: com.example.qingxue.rating.FormRatingSummary,
        tasks: List<StudyTaskEntity>,
        sessions: List<FocusSessionEntity>,
        context: android.content.Context
    ): AiAnalysisEntity {
        val result = com.example.qingxue.ai.AiAnalyzer.analyze(
            context = context,
            summary = summary,
            tasks = tasks,
            sessions = sessions,
            periodStart = periodStart,
            periodEnd = periodEnd
        )
        val entity = result.toEntity(date, periodStart, periodEnd)
        dao.insertAnalysis(entity)
        return entity
    }

    private data class QuotePayload(val text: String, val source: String)

    companion object {
        private const val QUOTE_URL =
            "https://v1.hitokoto.cn/?c=d&c=i&c=k&encode=json&charset=utf-8&max_length=36"

        private val FALLBACK_QUOTES = listOf(
            "把今天能做的一小步走完。",
            "专注当下，答案会在行动里出现。",
            "稳定地前进，比偶尔冲刺更可靠。",
            "开始之后，困难就会变得具体。",
            "完成比完美更接近目标。",
            "每一次专注，都在为未来积累底气。",
            "不必一下走很远，先走下一步。",
            "今天的认真，会成为明天的从容。",
            "把复杂的事，拆成一个个简单动作。",
            "真正的进步，常常安静而缓慢。",
            "给自己二十五分钟，只做一件事。",
            "先完成最重要的，再处理最紧急的。",
            "一页一页读，也能抵达很远的地方。",
            "坚持不是用力过猛，而是不轻易中断。",
            "清晰的目标，会让努力更有方向。",
            "把注意力放回你能够改变的事情。",
            "现在种下的耐心，会在以后开花。",
            "学习的回报，藏在一次次复习里。",
            "慢一点没关系，别停下来。",
            "最好的准备，是从此刻开始。",
            "一次只解决一个问题。",
            "小小的完成感，会带来下一次行动。",
            "时间不会辜负持续投入的人。",
            "把目标写下来，把行动放到今天。",
            "越接近目标，越要保持自己的节奏。",
            "无需等待状态，行动会带来状态。",
            "困难的章节，也会有翻过去的一页。",
            "认真度过今天，就是在缩短距离。",
            "保持好奇，也保持耐心。",
            "你重复练习的，终会成为你的能力。",
            "先做五分钟，让开始变得容易。"
        )
    }
}
