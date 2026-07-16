package com.example.qingxue.data

import org.json.JSONArray
import org.json.JSONObject

data class AppBackupData(
    val themeAccent: String?,
    val tasks: List<StudyTaskEntity>,
    val sessions: List<FocusSessionEntity>,
    val dailyMatches: List<DailyMatchEntity> = emptyList(),
    val countdownEvents: List<CountdownEventEntity>,
    val dailyQuotes: List<DailyQuoteEntity>,
    val aiAnalyses: List<AiAnalysisEntity>
)

object AppBackupCodec {
    private const val SCHEMA_VERSION = 2
    private const val APP_ID = "com.example.qingxue"

    fun encode(data: AppBackupData): String {
        return JSONObject().apply {
            put("appId", APP_ID)
            put("schemaVersion", SCHEMA_VERSION)
            put("exportedAt", System.currentTimeMillis())
            putNullable("themeAccent", data.themeAccent)
            put("tasks", JSONArray().apply { data.tasks.forEach { put(it.toJson()) } })
            put("sessions", JSONArray().apply { data.sessions.forEach { put(it.toJson()) } })
            put("dailyMatches", JSONArray().apply {
                data.dailyMatches.forEach { put(it.toJson()) }
            })
            put("countdownEvents", JSONArray().apply {
                data.countdownEvents.forEach { put(it.toJson()) }
            })
            put("dailyQuotes", JSONArray().apply { data.dailyQuotes.forEach { put(it.toJson()) } })
            put("aiAnalyses", JSONArray().apply { data.aiAnalyses.forEach { put(it.toJson()) } })
        }.toString(2)
    }

    fun decode(text: String): AppBackupData {
        val root = JSONObject(text)
        require(root.optString("appId") == APP_ID) { "Not a LOCK IN backup" }
        val schemaVersion = root.optInt("schemaVersion", -1)
        require(schemaVersion in 1..SCHEMA_VERSION) {
            "Unsupported backup version"
        }
        return AppBackupData(
            themeAccent = root.stringOrNull("themeAccent"),
            tasks = root.objects("tasks").map { it.toTask() },
            sessions = root.objects("sessions").map { it.toSession() },
            dailyMatches = root.objects("dailyMatches").map { it.toDailyMatch() },
            countdownEvents = root.objects("countdownEvents").map { it.toCountdownEvent() },
            dailyQuotes = root.objects("dailyQuotes").map { it.toDailyQuote() },
            aiAnalyses = root.objects("aiAnalyses").map { it.toAiAnalysis() }
        )
    }

    private fun StudyTaskEntity.toJson() = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("subject", subject)
        put("description", description)
        put("estimatedMinutes", estimatedMinutes)
        put("date", date)
        put("completed", completed)
        put("isHabit", isHabit)
        putNullable("lastCompletedDate", lastCompletedDate)
        put("studyType", studyType)
        put("isCore", isCore)
        putNullable("habitId", habitId)
        put("isArchived", isArchived)
        put("createdAt", createdAt)
    }

    private fun FocusSessionEntity.toJson() = JSONObject().apply {
        put("id", id)
        putNullable("taskId", taskId)
        putNullable("habitId", habitId)
        put("startedAt", startedAt)
        put("endedAt", endedAt)
        put("durationMinutes", durationMinutes)
        put("date", date)
        put("plannedMinutes", plannedMinutes)
        put("actualSeconds", actualSeconds)
        put("pauseCount", pauseCount)
        put("pausedSeconds", pausedSeconds)
        put("endReason", endReason)
        put("outcome", outcome)
        put("focusBlockMinutes", focusBlockMinutes)
        put("breakMinutes", breakMinutes)
        put("plannedCycles", plannedCycles)
        put("completedCycles", completedCycles)
        put("reflection", reflection)
        put("winCondition", winCondition)
        put("roundResult", roundResult)
        put("focusQuality", focusQuality)
        put("wentWell", wentWell)
        put("problemDescription", problemDescription)
        put("nextCall", nextCall)
        put("distractionCount", distractionCount)
    }

    private fun DailyMatchEntity.toJson() = JSONObject().apply {
        put("date", date)
        putNullable("mainTaskId", mainTaskId)
        put("manualObjective", manualObjective)
        put("plannedRounds", plannedRounds)
        put("userNote", userNote)
        put("createdAt", createdAt)
    }
    private fun CountdownEventEntity.toJson() = JSONObject().apply {
        put("id", id)
        put("title", title)
        put("targetDate", targetDate)
        put("description", description)
        put("isPinned", isPinned)
        put("createdAt", createdAt)
    }

    private fun DailyQuoteEntity.toJson() = JSONObject().apply {
        put("date", date)
        put("text", text)
        put("source", source)
        put("fetchedAt", fetchedAt)
        put("isOnline", isOnline)
        put("networkAttempted", networkAttempted)
    }

    private fun AiAnalysisEntity.toJson() = JSONObject().apply {
        put("id", id)
        put("date", date)
        put("periodStart", periodStart)
        put("periodEnd", periodEnd)
        put("overallComment", overallComment)
        put("dimensionAnalysis", dimensionAnalysis)
        put("advice", advice)
        put("createdAt", createdAt)
    }

    private fun JSONObject.toTask() = StudyTaskEntity(
        id = getLong("id"),
        title = getString("title"),
        subject = getString("subject"),
        description = optString("description"),
        estimatedMinutes = getInt("estimatedMinutes"),
        date = getString("date"),
        completed = optBoolean("completed"),
        isHabit = optBoolean("isHabit"),
        lastCompletedDate = stringOrNull("lastCompletedDate"),
        studyType = optString("studyType", StudyTaskType.General.storageValue),
        isCore = optBoolean("isCore"),
        habitId = longOrNull("habitId"),
        isArchived = optBoolean("isArchived"),
        createdAt = getLong("createdAt")
    )

    private fun JSONObject.toSession() = FocusSessionEntity(
        id = getLong("id"),
        taskId = longOrNull("taskId"),
        habitId = longOrNull("habitId"),
        startedAt = getLong("startedAt"),
        endedAt = getLong("endedAt"),
        durationMinutes = getInt("durationMinutes"),
        date = getString("date"),
        plannedMinutes = optInt("plannedMinutes"),
        actualSeconds = optInt("actualSeconds"),
        pauseCount = optInt("pauseCount"),
        pausedSeconds = optInt("pausedSeconds"),
        endReason = optString("endReason", FocusEndReason.Completed.storageValue),
        outcome = optString("outcome", FocusOutcome.Unreviewed.storageValue),
        focusBlockMinutes = optInt("focusBlockMinutes"),
        breakMinutes = optInt("breakMinutes"),
        plannedCycles = optInt("plannedCycles", 1),
        completedCycles = optInt("completedCycles"),
        reflection = optString("reflection"),
        winCondition = optString("winCondition"),
        roundResult = optString("roundResult", RoundResult.Unreviewed.storageValue),
        focusQuality = optString("focusQuality", FocusQuality.Unreviewed.storageValue),
        wentWell = optString("wentWell"),
        problemDescription = optString("problemDescription"),
        nextCall = optString("nextCall"),
        distractionCount = optInt("distractionCount")
    )

    private fun JSONObject.toDailyMatch() = DailyMatchEntity(
        date = getString("date"),
        mainTaskId = longOrNull("mainTaskId"),
        manualObjective = optString("manualObjective"),
        plannedRounds = optInt("plannedRounds", 2),
        userNote = optString("userNote"),
        createdAt = optLong("createdAt", System.currentTimeMillis())
    )
    private fun JSONObject.toCountdownEvent() = CountdownEventEntity(
        id = getLong("id"),
        title = getString("title"),
        targetDate = getString("targetDate"),
        description = optString("description"),
        isPinned = optBoolean("isPinned"),
        createdAt = getLong("createdAt")
    )

    private fun JSONObject.toDailyQuote() = DailyQuoteEntity(
        date = getString("date"),
        text = getString("text"),
        source = getString("source"),
        fetchedAt = getLong("fetchedAt"),
        isOnline = optBoolean("isOnline"),
        networkAttempted = optBoolean("networkAttempted")
    )

    private fun JSONObject.toAiAnalysis() = AiAnalysisEntity(
        id = getLong("id"),
        date = getString("date"),
        periodStart = optString("periodStart"),
        periodEnd = optString("periodEnd"),
        overallComment = getString("overallComment"),
        dimensionAnalysis = getString("dimensionAnalysis"),
        advice = getString("advice"),
        createdAt = getLong("createdAt")
    )

    private fun JSONObject.objects(key: String): List<JSONObject> {
        val array = optJSONArray(key) ?: JSONArray()
        return (0 until array.length()).map { array.getJSONObject(it) }
    }

    private fun JSONObject.putNullable(key: String, value: Any?) {
        put(key, value ?: JSONObject.NULL)
    }

    private fun JSONObject.stringOrNull(key: String): String? {
        return if (!has(key) || isNull(key)) null else getString(key)
    }

    private fun JSONObject.longOrNull(key: String): Long? {
        return if (!has(key) || isNull(key)) null else getLong(key)
    }
}