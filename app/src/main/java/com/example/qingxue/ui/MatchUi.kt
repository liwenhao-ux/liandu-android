package com.example.qingxue.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qingxue.data.DailyMatchEntity
import com.example.qingxue.data.DemoReview
import com.example.qingxue.data.FocusQuality
import com.example.qingxue.data.FocusSessionEntity
import com.example.qingxue.data.RoundResult
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.focus.PendingFocusSettlement
import java.time.LocalTime

internal data class DailyMatchPresentation(
    val objective: String,
    val taskId: Long?,
    val status: String,
    val message: String,
    val completedRounds: Int,
    val plannedRounds: Int,
    val progress: Float
)

internal fun dailyMatchPresentation(
    match: DailyMatchEntity?,
    tasks: List<StudyTaskEntity>,
    sessions: List<FocusSessionEntity>
): DailyMatchPresentation {
    val task = match?.mainTaskId?.let { id -> tasks.firstOrNull { it.id == id } }
    val objective = task?.title ?: match?.manualObjective?.takeIf { it.isNotBlank() }
        ?: tasks.firstOrNull { it.isCore && !it.completed }?.title
        ?: "选择今天最重要的一回合"
    val planned = match?.plannedRounds?.coerceIn(1, 8) ?: 2
    val completed = sessions.count { it.actualSeconds >= 60 || it.durationMinutes > 0 }
    val taskWon = task?.completed == true
    val manualWon = task == null && match?.manualObjective?.isNotBlank() == true &&
        sessions.any {
            it.taskId == null &&
                it.habitId == null &&
                it.winCondition == match.manualObjective &&
                RoundResult.fromStorage(it.roundResult) == RoundResult.Win
        }
    val results = sessions.sortedBy { it.startedAt }.map { RoundResult.fromStorage(it.roundResult) }
    val comeback = results.indexOfFirst {
        it == RoundResult.Loss || it == RoundResult.PartialWin
    }.let { setback -> setback >= 0 && results.drop(setback + 1).any { it == RoundResult.Win } }
    val status = when {
        taskWon || manualWon -> "Victory"
        completed == 0 -> "Warm-up"
        comeback -> "Comeback Started"
        LocalTime.now().hour >= 18 && completed < planned -> "Falling Behind"
        else -> "In the Match"
    }
    val message = when (status) {
        "Victory" -> "主目标已拿下。保存体力，准备下一场。"
        "Comeback Started" -> "比分已经开始改变。继续叫好下一回合。"
        "Falling Behind" -> "落后一回合不是输掉比赛。完成一个有意义的动作。"
        "In the Match" -> "别看最终比分，把下一回合打好。"
        else -> "定义胜利条件，然后开局。"
    }
    return DailyMatchPresentation(
        objective = objective,
        taskId = task?.id ?: match?.mainTaskId,
        status = status,
        message = message,
        completedRounds = completed,
        plannedRounds = planned,
        progress = (completed.toFloat() / planned).coerceIn(0f, 1f)
    )
}

@Composable
internal fun TodayRoundCard(
    match: DailyMatchEntity?,
    tasks: List<StudyTaskEntity>,
    sessions: List<FocusSessionEntity>,
    onChoose: () -> Unit,
    onStart: (Long?) -> Unit
) {
    val p = dailyMatchPresentation(match, tasks, sessions)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().tacticalCamoPattern().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "TODAY'S ROUND",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        p.status,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.68f)
                    )
                }
                IconButton(onClick = onChoose) {
                    Icon(Icons.Filled.Edit, contentDescription = "选择今日主目标")
                }
            }
            Text(
                p.objective,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "回合 ${p.completedRounds}/${p.plannedRounds} · ${p.message}",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
            )
            LinearProgressIndicator(
                progress = { p.progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f)
            )
            Button(onClick = { onStart(p.taskId) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("开始回合")
            }
        }
    }
}

@Composable
internal fun TodayRoundPickerDialog(
    current: DailyMatchEntity?,
    tasks: List<StudyTaskEntity>,
    onDismiss: () -> Unit,
    onSave: (Long?, String, Int) -> Unit
) {
    var selectedTaskId by rememberSaveable(current?.date) { mutableStateOf(current?.mainTaskId) }
    var manualObjective by rememberSaveable(current?.date) {
        mutableStateOf(current?.manualObjective.orEmpty())
    }
    var plannedRounds by rememberSaveable(current?.date) {
        mutableIntStateOf(current?.plannedRounds ?: 2)
    }
    val activeTasks = tasks.filter { !it.isArchived && !it.completed }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择 Today's Round") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("任务或习惯", fontWeight = FontWeight.SemiBold)
                if (activeTasks.isEmpty()) {
                    Text(
                        "当前没有可选任务，也可以直接写一个目标。",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                        fontSize = 13.sp
                    )
                } else {
                    activeTasks.forEach { task ->
                        FilterChip(
                            selected = selectedTaskId == task.id,
                            onClick = {
                                selectedTaskId = task.id
                                manualObjective = ""
                            },
                            label = {
                                Text(task.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            },
                            leadingIcon = if (task.isCore) {
                                { Icon(Icons.Filled.Flag, contentDescription = null) }
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                OutlinedTextField(
                    value = manualObjective,
                    onValueChange = {
                        manualObjective = it.take(80)
                        if (it.isNotBlank()) selectedTaskId = null
                    },
                    label = { Text("或手动输入目标") },
                    placeholder = { Text("例如：完成 Kotlin 统计页") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Text("计划回合", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..4).forEach { count ->
                        FilterChip(
                            selected = plannedRounds == count,
                            onClick = { plannedRounds = count },
                            label = { Text("$count 回合") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedTaskId, manualObjective, plannedRounds)
                    onDismiss()
                },
                enabled = selectedTaskId != null || manualObjective.isNotBlank()
            ) { Text("确认") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
internal fun PreRoundDialog(
    initialWinCondition: String,
    onDismiss: () -> Unit,
    onAdjust: () -> Unit,
    onStart: (String) -> Unit
) {
    var winCondition by rememberSaveable(initialWinCondition) {
        mutableStateOf(initialWinCondition.take(160))
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Flag, contentDescription = null) },
        title = { Text("这一回合怎样算赢？") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "不要想整场比赛，只打好这一回合。",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
                OutlinedTextField(
                    value = winCondition,
                    onValueChange = { winCondition = it.take(160) },
                    label = { Text("胜利条件") },
                    placeholder = { Text("例如：做完 5 道微积分题") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onStart(winCondition.trim()) },
                enabled = winCondition.isNotBlank()
            ) { Text("Start Round") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    onDismiss()
                    onAdjust()
                }) { Text("调整任务与时间") }
                TextButton(onClick = onDismiss) { Text("取消") }
            }
        }
    )
}

@Composable
internal fun DemoReviewDialog(
    settlement: PendingFocusSettlement,
    onSave: (DemoReview) -> Unit,
    onSkip: () -> Unit
) {
    var result by rememberSaveable(settlement.sessionId) { mutableStateOf(RoundResult.Unreviewed) }
    var quality by rememberSaveable(settlement.sessionId) { mutableStateOf(FocusQuality.Unreviewed) }
    var wentWell by rememberSaveable(settlement.sessionId) { mutableStateOf("") }
    var problems by rememberSaveable(settlement.sessionId) { mutableStateOf("") }
    var nextCall by rememberSaveable(settlement.sessionId) { mutableStateOf("") }
    var distractions by rememberSaveable(settlement.sessionId) { mutableStateOf("0") }
    AlertDialog(
        onDismissRequest = onSkip,
        title = { Text("Demo Review") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    settlement.taskTitle ?: "自由专注",
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (settlement.winCondition.isNotBlank()) {
                    Text(
                        "胜利条件：${settlement.winCondition}",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        fontSize = 13.sp
                    )
                }
                Text(
                    "${settlement.actualMinutes} 分钟 · ${if (settlement.completedTimer) "自然结束" else "提前结束"}",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
                ChoiceRow(
                    title = "结果",
                    items = RoundResult.entries.filterNot { it == RoundResult.Unreviewed },
                    selected = result,
                    label = { it.label },
                    onSelect = { result = it }
                )
                ChoiceRow(
                    title = "专注质量",
                    items = FocusQuality.entries.filterNot { it == FocusQuality.Unreviewed },
                    selected = quality,
                    label = { it.label },
                    onSelect = { quality = it }
                )
                ReviewField("做得好的地方", wentWell, "哪一步值得保留？") { wentWell = it }
                ReviewField("出现的问题", problems, "是什么打断了节奏？") { problems = it }
                ReviewField("Next Call", nextCall, "下一回合只做什么？") { nextCall = it }
                OutlinedTextField(
                    value = distractions,
                    onValueChange = { distractions = it.filter(Char::isDigit).take(2) },
                    label = { Text("分心次数") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        DemoReview(
                            result = result,
                            focusQuality = quality,
                            wentWell = wentWell,
                            problemDescription = problems,
                            nextCall = nextCall,
                            distractionCount = distractions.toIntOrNull() ?: 0
                        )
                    )
                },
                enabled = result != RoundResult.Unreviewed &&
                    quality != FocusQuality.Unreviewed
            ) { Text("保存复盘") }
        },
        dismissButton = { TextButton(onClick = onSkip) { Text("稍后复盘") } }
    )
}

@Composable
private fun <T> ChoiceRow(
    title: String,
    items: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                FilterChip(
                    selected = selected == item,
                    onClick = { onSelect(item) },
                    label = { Text(label(item)) }
                )
            }
        }
    }
}

@Composable
private fun ReviewField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it.take(500)) },
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 4
    )
}

@Composable
internal fun ComebackReminderCard() {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Refresh,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text("CALL THE NEXT ROUND", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(
                    "当前比分不是最终结果。一次只打好下一回合。",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
