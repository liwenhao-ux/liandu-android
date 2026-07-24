package com.example.qingxue.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.qingxue.data.StudyTaskEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
internal fun ManualFocusDialog(
    tasks: List<StudyTaskEntity>,
    onDismiss: () -> Unit,
    onSave: (Long?, Long, Int, String) -> Unit
) {
    val context = LocalContext.current
    val zone = ZoneId.systemDefault()
    val defaultStart = remember {
        ZonedDateTime.now(zone).minusMinutes(25).withSecond(0).withNano(0).toLocalDateTime()
    }
    var dateText by rememberSaveable { mutableStateOf(defaultStart.toLocalDate().toString()) }
    var timeText by rememberSaveable { mutableStateOf(defaultStart.toLocalTime().toString()) }
    var durationText by rememberSaveable { mutableStateOf("25") }
    var selectedTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var reflection by rememberSaveable { mutableStateOf("") }

    val selectedDate = runCatching { LocalDate.parse(dateText) }.getOrNull()
    val selectedTime = runCatching { LocalTime.parse(timeText) }.getOrNull()
    val duration = durationText.toIntOrNull()
    val startedAt = if (selectedDate != null && selectedTime != null) {
        LocalDateTime.of(selectedDate, selectedTime).atZone(zone).toInstant().toEpochMilli()
    } else {
        null
    }
    val endedAt = if (startedAt != null && duration != null) {
        startedAt + duration * 60_000L
    } else {
        null
    }
    val durationValid = duration != null && duration in 1..960
    val timeValid = endedAt != null && endedAt <= System.currentTimeMillis()
    val formValid = durationValid && timeValid
    val dateLabel = selectedDate?.format(DateTimeFormatter.ofPattern("yyyy/M/d")) ?: "选择日期"
    val timeLabel = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "选择时间"
    val availableTasks = tasks.filterNot { it.isArchived }.sortedWith(
        compareBy<StudyTaskEntity> { it.completed }.thenByDescending { it.isHabit }.thenBy { it.title }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("补记专注") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 560.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "填写实际开始时间和持续时长，记录会正常计入任务、习惯与统计。",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val initial = selectedDate ?: LocalDate.now(zone)
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dateText = LocalDate.of(year, month + 1, day).toString()
                                },
                                initial.year,
                                initial.monthValue - 1,
                                initial.dayOfMonth
                            ).apply {
                                datePicker.maxDate = System.currentTimeMillis()
                            }.show()
                        }
                    ) { Text(dateLabel) }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val initial = selectedTime ?: LocalTime.now(zone)
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    timeText = LocalTime.of(hour, minute).toString()
                                },
                                initial.hour,
                                initial.minute,
                                true
                            ).show()
                        }
                    ) { Text(timeLabel) }
                }
                Text(
                    "凌晨 4 点前的记录会归入前一个学习日。",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it.filter(Char::isDigit).take(3) },
                    label = { Text("专注时长（分钟）") },
                    supportingText = {
                        when {
                            durationText.isNotBlank() && !durationValid -> Text("请输入 1–960 分钟")
                            durationValid && !timeValid -> Text("结束时间不能晚于现在")
                        }
                    },
                    isError = durationText.isNotBlank() && (!durationValid || !timeValid),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("关联任务或习惯", fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTaskId == null,
                        onClick = { selectedTaskId = null },
                        label = { Text("不关联") }
                    )
                    availableTasks.forEach { task ->
                        FilterChip(
                            selected = selectedTaskId == task.id,
                            onClick = { selectedTaskId = task.id },
                            label = {
                                Text(
                                    (if (task.isHabit) "习惯 · " else "任务 · ") + task.title,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
                OutlinedTextField(
                    value = reflection,
                    onValueChange = { reflection = it.take(500) },
                    label = { Text("备注（可选）") },
                    placeholder = { Text("例如：图书馆复习高数例题") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                Spacer(Modifier.height(2.dp))
            }
        },
        confirmButton = {
            Button(
                enabled = formValid,
                onClick = {
                    onSave(selectedTaskId, checkNotNull(startedAt), checkNotNull(duration), reflection)
                }
            ) {
                Text("保存补记")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}