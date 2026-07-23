package com.example.qingxue.ui

import com.example.qingxue.util.studyDate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qingxue.data.FocusEndReason
import com.example.qingxue.data.FocusOutcome
import com.example.qingxue.data.FocusQuality
import com.example.qingxue.data.RoundResult
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.HabitProgressCalculator
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.data.StudyTaskType
import com.example.qingxue.ui.theme.AppVisualStyle
import com.example.qingxue.ui.theme.LocalAppVisualStyle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private enum class HistoryRange(val label: String, val days: Long?) {
    SevenDays("7 天", 7), ThirtyDays("30 天", 30), All("全部", null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FocusHistoryScreen(
    history: StudyHistoryState,
    onSaveReflection: (Long, String) -> Unit = { _, _ -> }
) {
    var range by rememberSaveable { mutableStateOf(HistoryRange.ThirtyDays) }
    var selectedTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var detailSession by rememberSaveable { mutableStateOf<FocusSessionEntity?>(null) }
    val tasksById = history.tasks.associateBy { it.id }
    val today = studyDate()
    val filterTasks = history.sessions.mapNotNull { session ->
        session.taskId?.let(tasksById::get) ?: session.habitId?.let(tasksById::get)
    }.distinctBy { it.id }
    val sessions = history.sessions.filter { session ->
        val inRange = range.days?.let { days ->
            runCatching { LocalDate.parse(session.date) >= today.minusDays(days - 1) }.getOrDefault(false)
        } ?: true
        inRange && (
            selectedTaskId == null ||
                session.taskId == selectedTaskId ||
                session.habitId == selectedTaskId
            )
    }
    val totalSeconds = sessions.sumOf { it.effectiveSeconds() }
    val completedCount = sessions.count { it.endReason == FocusEndReason.Completed.storageValue }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                HistoryRange.entries.forEachIndexed { index, item ->
                    SegmentedButton(
                        selected = range == item,
                        onClick = { range = item },
                        shape = SegmentedButtonDefaults.itemShape(index, HistoryRange.entries.size)
                    ) { Text(item.label) }
                }
            }
        }
        if (filterTasks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTaskId == null,
                        onClick = { selectedTaskId = null },
                        label = { Text("全部任务") }
                    )
                    filterTasks.forEach { task ->
                        FilterChip(
                            selected = selectedTaskId == task.id,
                            onClick = { selectedTaskId = task.id },
                            label = { Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        )
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("总专注", formatDuration(totalSeconds), Modifier.weight(1f))
                MetricCard("有效回合", "$completedCount/${sessions.size}", Modifier.weight(1f))
            }
        }
        item { Text("回合记录", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) }
        if (sessions.isEmpty()) {
            item { DetailCard { MutedText("这个范围内还没有回合记录。") } }
        } else {
            items(sessions, key = { "history-${it.id}" }) { session ->
                SessionCard(
                    session = session,
                    taskTitle = sessionDisplayTitle(session, tasksById),
                    onClick = { detailSession = session }
                )
            }
        }
    }

    detailSession?.let { session ->
        SessionDetailPopup(
            session = session,
            taskTitle = sessionDisplayTitle(session, tasksById),
            onDismiss = { detailSession = null },
            onSaveReflection = { reflection ->
                onSaveReflection(session.id, reflection)
                detailSession = detailSession?.copy(reflection = reflection)
            }
        )
    }
}

@Composable
internal fun TaskDetailScreen(
    task: StudyTaskEntity,
    allTasks: List<StudyTaskEntity>,
    sessions: List<FocusSessionEntity>,
    onEdit: () -> Unit,
    onStartFocus: () -> Unit,
    onAddTodayTask: () -> Unit,
    onOpenTask: (Long) -> Unit
) {
    val today = studyDate()
    val todayText = today.toString()
    val tasksById = allTasks.associateBy { it.id }
    val taskSessions = if (task.isHabit) {
        sessions.filter { it.habitId == task.id }
    } else {
        sessions.filter { it.taskId == task.id }
    }
    val seven = taskSessions.filter { it.dateOnOrAfter(today.minusDays(6)) }
    val thirty = taskSessions.filter { it.dateOnOrAfter(today.minusDays(29)) }
    val activeDays = taskSessions.map { it.date }.distinct().size
    val habitProgress = if (task.isHabit) {
        HabitProgressCalculator.calculate(
            sessions = sessions,
            habitId = task.id,
            dailyTargetMinutes = task.estimatedMinutes,
            today = today
        )
    } else {
        null
    }
    val todaySeconds = habitProgress?.todaySeconds ?: 0
    val totalSeconds = habitProgress?.totalSeconds
        ?: taskSessions.sumOf { it.effectiveSeconds() }
    val targetSeconds = task.estimatedMinutes * 60
    val habitCompleted = habitProgress?.completedToday == true
    val parentHabit = task.habitId?.let(tasksById::get)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            DetailCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Spacer(Modifier.height(4.dp))
                        MutedText(taskMeta(task, parentHabit?.title))
                    }
                    Text(
                        when {
                            task.isHabit && habitCompleted -> "今日完成"
                            task.isHabit -> "进行中"
                            task.completed -> "已完成"
                            else -> "进行中"
                        },
                        color = if (habitCompleted || task.completed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (task.description.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(task.description, lineHeight = 21.sp)
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Edit, null)
                        Spacer(Modifier.width(6.dp))
                        Text("编辑")
                    }
                    Button(onClick = onStartFocus, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PlayArrow, null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (task.isHabit) "直接专注" else "专注")
                    }
                }
            }
        }

        if (task.isHabit) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        "今日累计",
                        formatDuration(todaySeconds),
                        Modifier.weight(1f)
                    )
                    MetricCard(
                        "今日目标",
                        task.estimatedMinutes.toString() + " 分钟",
                        Modifier.weight(1f)
                    )
                }
            }
            item {
                DetailCard {
                    val progress = if (targetSeconds <= 0) {
                        0f
                    } else {
                        todaySeconds.toFloat() / targetSeconds
                    }
                    LinearProgressIndicator(
                        progress = { progress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    MutedText(
                        if (habitCompleted) {
                            "今日目标已完成"
                        } else {
                            "还差 " + ((targetSeconds - todaySeconds).coerceAtLeast(0) + 59) / 60 +
                                " 分钟"
                        }
                    )
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        "连续完成",
                        (habitProgress?.streakDays ?: 0).toString() + " 天",
                        Modifier.weight(1f)
                    )
                    MetricCard(
                        "累计投入",
                        formatDuration(totalSeconds),
                        Modifier.weight(1f)
                    )
                }
            }

            val todayTasks = allTasks.filter {
                !it.isHabit && !it.isArchived && it.habitId == task.id && it.date == todayText
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "今日任务",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        todayTasks.count { it.completed }.toString() + "/" + todayTasks.size,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            if (todayTasks.isEmpty()) {
                item { DetailCard { MutedText("今天还没有关联任务，可以直接专注或添加一项。") } }
            } else {
                items(todayTasks, key = { "habit-task-" + it.id }) { linkedTask ->
                    val taskTodaySeconds = sessions
                        .filter { it.taskId == linkedTask.id && it.date == todayText }
                        .sumOf { it.effectiveSeconds() }
                    HabitTaskRow(
                        task = linkedTask,
                        seconds = taskTodaySeconds,
                        onClick = { onOpenTask(linkedTask.id) }
                    )
                }
            }
            item {
                OutlinedButton(
                    onClick = onAddTodayTask,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("添加今日任务")
                }
            }
        } else {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        "累计",
                        formatDuration(totalSeconds),
                        Modifier.weight(1f)
                    )
                    MetricCard("活跃", activeDays.toString() + " 天", Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        "近 7 天",
                        formatDuration(seven.sumOf { it.effectiveSeconds() }),
                        Modifier.weight(1f)
                    )
                    MetricCard(
                        "近 30 天",
                        formatDuration(thirty.sumOf { it.effectiveSeconds() }),
                        Modifier.weight(1f)
                    )
                }
            }
        }

        item { Text("最近记录", fontWeight = FontWeight.SemiBold, fontSize = 18.sp) }
        if (taskSessions.isEmpty()) {
            item { DetailCard { MutedText("还没有绑定到这里的专注记录。") } }
        } else {
            items(taskSessions.take(12), key = { "task-history-" + it.id }) { session ->
                SessionCard(
                    session,
                    sessionDisplayTitle(session, tasksById)
                )
            }
        }
    }
}

@Composable
private fun HabitTaskRow(
    task: StudyTaskEntity,
    seconds: Int,
    onClick: () -> Unit
) {
    DetailCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (task.completed) "✓" else "○",
                color = if (task.completed) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f)
                },
                fontSize = 20.sp,
                modifier = Modifier.width(30.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    task.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                MutedText(StudyTaskType.fromStorage(task.studyType).label)
            }
            Text(
                formatDuration(seconds),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
@Composable
private fun SessionCard(
    session: FocusSessionEntity,
    taskTitle: String?,
    onClick: (() -> Unit)? = null
) {
    val isTacticalStyle = LocalAppVisualStyle.current == AppVisualStyle.Tactical
    val outcome = FocusOutcome.fromStorage(session.outcome)
    val roundResult = RoundResult.fromStorage(session.roundResult)
    val completed = session.endReason == FocusEndReason.Completed.storageValue
    DetailCard(
        modifier = if (onClick != null) Modifier.fillMaxWidth().clickable(onClick = onClick) else Modifier.fillMaxWidth()
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    taskTitle ?: "未绑定任务",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                MutedText(formatSessionTime(session.startedAt))
            }
            Text(
                formatDuration(session.effectiveSeconds()),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(10.dp))
        MutedText(
            (if (completed) "完成计划" else "提前结束") +
                " · 计划 ${session.plannedMinutes} 分钟" +
                if (session.pauseCount > 0) " · 暂停 ${session.pauseCount} 次" else ""
        )
        if (session.winCondition.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            MutedText("胜利条件：${session.winCondition}")
        }
        if (roundResult != RoundResult.Unreviewed) {
            Spacer(Modifier.height(4.dp))
            Text(
                if (isTacticalStyle) "ROUND · ${roundResult.label}" else "专注结果 · ${roundResult.label}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (outcome != FocusOutcome.Unreviewed) {
            Spacer(Modifier.height(4.dp))
            Text(
                "历史结算：${outcome.label}",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(14.dp)) {
            MutedText(label)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, maxLines = 1)
        }
    }
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier.fillMaxWidth(),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) { Column(Modifier.padding(16.dp), content = content) }
}

@Composable
private fun MutedText(text: String) {
    Text(text, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.66f), fontSize = 13.sp)
}

private fun FocusSessionEntity.effectiveSeconds() =
    actualSeconds.takeIf { it > 0 } ?: durationMinutes * 60

private fun FocusSessionEntity.dateOnOrAfter(minDate: LocalDate) =
    runCatching { LocalDate.parse(date) >= minDate }.getOrDefault(false)

private fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val hours = minutes / 60
    val rest = minutes % 60
    return when {
        hours == 0 -> "$minutes 分钟"
        rest == 0 -> "$hours 小时"
        else -> "$hours 小时 $rest 分"
    }
}

private fun formatSessionTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MM月dd日 HH:mm", Locale.CHINA)
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(formatter)
}

@Composable
private fun SessionDetailPopup(
    session: FocusSessionEntity,
    taskTitle: String?,
    onDismiss: () -> Unit,
    onSaveReflection: (String) -> Unit
) {
    val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormat = DateTimeFormatter.ofPattern("yyyy年M月d日")
    val zone = ZoneId.systemDefault()
    val startTime = Instant.ofEpochMilli(session.startedAt).atZone(zone).toLocalTime()
    val endTime = Instant.ofEpochMilli(session.endedAt).atZone(zone).toLocalTime()
    val date = Instant.ofEpochMilli(session.startedAt).atZone(zone).toLocalDate()
    val outcome = FocusOutcome.fromStorage(session.outcome)
    val roundResult = RoundResult.fromStorage(session.roundResult)
    val quality = FocusQuality.fromStorage(session.focusQuality)
    var reflection by rememberSaveable(key = session.id.toString()) { mutableStateOf(session.reflection) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(taskTitle ?: "自由专注") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "${date.format(dateFormat)}  ${startTime.format(timeFormat)}–${endTime.format(timeFormat)}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
                Text(
                    "${session.durationMinutes} 分钟${if (session.pauseCount > 0) " · 暂停${session.pauseCount}次" else ""}" +
                        " · ${if (session.endReason == "COMPLETED") "自然完成" else "提前结束"}",
                    fontSize = 13.sp
                )
                if (roundResult != RoundResult.Unreviewed) {
                    Text(
                        "${roundResult.label} · 专注质量 ${quality.label}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else if (outcome != FocusOutcome.Unreviewed) {
                    Text("历史结算：${outcome.label}", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                }
                if (session.winCondition.isNotBlank()) MutedText("胜利条件：${session.winCondition}")
                if (session.wentWell.isNotBlank()) MutedText("做得好：${session.wentWell}")
                if (session.problemDescription.isNotBlank()) MutedText("问题：${session.problemDescription}")
                if (session.nextCall.isNotBlank()) MutedText("Next Call：${session.nextCall}")
                if (session.distractionCount > 0) MutedText("分心 ${session.distractionCount} 次")
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it.take(500) },
                    label = { Text("补充说明") },
                    placeholder = { Text("效率、难度、精力、情绪、环境…") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSaveReflection(reflection)
                onDismiss()
            }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

private fun sessionDisplayTitle(
    session: FocusSessionEntity,
    tasksById: Map<Long, StudyTaskEntity>
): String? {
    session.taskId?.let { taskId ->
        return tasksById[taskId]?.title
    }
    session.habitId?.let { habitId ->
        return tasksById[habitId]?.title?.plus(" · 自由学习")
    }
    return null
}

private fun taskMeta(task: StudyTaskEntity, habitTitle: String? = null): String {
    val kind = if (task.isHabit) "长期习惯" else "一次任务"
    val core = if (task.isCore) " · 核心" else ""
    val target = if (task.isHabit) {
        " · 今日目标 " + task.estimatedMinutes + " 分钟"
    } else {
        " · " + task.estimatedMinutes + " 分钟"
    }
    val habit = habitTitle?.let { " · 所属习惯 " + it }.orEmpty()
    return kind + " · " + StudyTaskType.fromStorage(task.studyType).label + core +
        " · " + task.subject + target + habit
}