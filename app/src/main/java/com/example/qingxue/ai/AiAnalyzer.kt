package com.example.qingxue.ai

import android.content.Context
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.rating.FormRatingSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AiAnalyzer {
    private const val API_URL = "https://api.deepseek.com/chat/completions"

    suspend fun analyze(
        context: Context,
        summary: FormRatingSummary,
        tasks: List<StudyTaskEntity>,
        sessions: List<FocusSessionEntity>,
        periodStart: String,
        periodEnd: String
    ): AiAnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = ApiKeyManager.getApiKey(context)
        if (apiKey.isBlank()) throw IllegalStateException("请先在设置中配置 DeepSeek API Key")

        val systemPrompt = buildSystemPrompt()
        val userData = buildUserData(summary, tasks, sessions, periodStart, periodEnd)

        val requestBody = JSONObject().apply {
            put("model", "deepseek-v4-flash")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userData)
                })
            })
            put("temperature", 0.7)
            put("max_tokens", 800)
        }

        val connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 30_000
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $apiKey")
            setRequestProperty("Accept", "application/json")
            doOutput = true
        }

        try {
            connection.outputStream.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }

            if (connection.responseCode !in 200..299) {
                val errorBody = connection.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() } ?: ""
                throw IllegalStateException("API 请求失败 (${connection.responseCode}): $errorBody")
            }

            val responseBody = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            parseResponse(responseBody)
        } catch (e: Exception) {
            if (e is IllegalStateException) throw e
            val detail = when (e) {
                is java.net.UnknownHostException -> "无法连接服务器，请检查网络"
                is java.net.SocketTimeoutException -> "请求超时，请重试"
                is javax.net.ssl.SSLException -> "安全连接失败"
                else -> "${e.javaClass.simpleName}: ${e.message}"
            }
            throw IllegalStateException(detail)
        } finally {
            connection.disconnect()
        }
    }

    private fun buildSystemPrompt(): String = buildString {
        append("你是一位经验丰富的学习教练，擅长用简短、温暖、有洞察力的语言帮助学习者成长。")
        append("你会收到用户近期的学习数据。请分析并返回纯 JSON（不要 markdown 代码块），格式如下：\n")
        append("""{"overallComment":"总评（2-3句，温暖直接）","dimensions":{"execution":"任务执行维度点评","focus":"专注过程维度点评","consistency":"稳定性维度点评","impact":"核心影响维度点评"},"advice":"一条最具体可执行的改进建议"}""")
    }

    private fun buildUserData(
        summary: FormRatingSummary,
        tasks: List<StudyTaskEntity>,
        sessions: List<FocusSessionEntity>,
        periodStart: String,
        periodEnd: String
    ): String = buildString {
        append("【学习数据：$periodStart 至 $periodEnd】\n\n")

        append("FORM 综合评分：")
        append(summary.rating?.let { String.format("%.2f", it) } ?: "校准中")
        append("（可信度：${summary.confidence.label}）\n")
        append("四维度分：任务执行 ${(summary.execution * 100).toInt()}、")
        append("专注过程 ${(summary.focus * 100).toInt()}、")
        append("稳定性 ${(summary.consistency * 100).toInt()}、")
        append("核心影响 ${(summary.impact * 100).toInt()}\n\n")

        if (tasks.isNotEmpty()) {
            append("任务列表：\n")
            tasks.take(20).forEach { task ->
                val status = if (task.completed) "✓" else "○"
                val core = if (task.isCore) "[核心]" else ""
                val habit = if (task.isHabit) "[习惯]" else ""
                append("- $status$core$habit ${task.title}（${task.subject}，${task.estimatedMinutes}分钟）\n")
            }
        }

        if (sessions.isNotEmpty()) {
            append("\n专注记录（最近 ${minOf(sessions.size, 15)} 条）：\n")
            sessions.takeLast(15).forEach { session ->
                val mins = session.durationMinutes
                val pauses = if (session.pauseCount > 0) "，暂停${session.pauseCount}次" else ""
                val completed = session.completedCycles >= session.plannedCycles
                val taskTitle = tasks.firstOrNull { it.id == session.taskId }?.title
                    ?: tasks.firstOrNull { it.id == session.habitId }?.title?.plus("（自由学习）")
                    ?: "自由专注"
                append("- ${mins}分钟$pauses，${if (completed) "按计划完成" else "提前结束"}，任务：${taskTitle}")
                if (session.reflection.isNotBlank()) {
                    append("，感受：「${session.reflection}」")
                }
                append("\n")
            }
        }
    }

    private fun parseResponse(responseBody: String): AiAnalysisResult {
        val json = JSONObject(responseBody)
        val choices = json.getJSONArray("choices")
        val content = choices.getJSONObject(0).getJSONObject("message").getString("content")
        val cleaned = content.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val result = JSONObject(cleaned)
        val dims = result.getJSONObject("dimensions")
        return AiAnalysisResult(
            overallComment = result.getString("overallComment"),
            executionNote = dims.optString("execution", ""),
            focusNote = dims.optString("focus", ""),
            consistencyNote = dims.optString("consistency", ""),
            impactNote = dims.optString("impact", ""),
            advice = result.getString("advice")
        )
    }
}

data class AiAnalysisResult(
    val overallComment: String,
    val executionNote: String,
    val focusNote: String,
    val consistencyNote: String,
    val impactNote: String,
    val advice: String
) {
    fun toEntity(date: String, periodStart: String, periodEnd: String): com.example.qingxue.data.AiAnalysisEntity {
        val dimsJson = JSONObject().apply {
            put("execution", executionNote)
            put("focus", focusNote)
            put("consistency", consistencyNote)
            put("impact", impactNote)
        }
        return com.example.qingxue.data.AiAnalysisEntity(
            date = date,
            periodStart = periodStart,
            periodEnd = periodEnd,
            overallComment = overallComment,
            dimensionAnalysis = dimsJson.toString(),
            advice = advice
        )
    }

    companion object {
        fun fromEntity(entity: com.example.qingxue.data.AiAnalysisEntity): AiAnalysisResult {
            val dims = try {
                JSONObject(entity.dimensionAnalysis)
            } catch (_: Exception) {
                JSONObject()
            }
            return AiAnalysisResult(
                overallComment = entity.overallComment,
                executionNote = dims.optString("execution", ""),
                focusNote = dims.optString("focus", ""),
                consistencyNote = dims.optString("consistency", ""),
                impactNote = dims.optString("impact", ""),
                advice = entity.advice
            )
        }
    }
}
