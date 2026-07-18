package com.example.qingxue.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowInsets as AndroidWindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.qingxue.QingXueApp
import com.example.qingxue.ai.AiAnalysisResult
import com.example.qingxue.ai.ApiKeyManager
import com.example.qingxue.data.AiAnalysisEntity
import com.example.qingxue.data.CountdownEventEntity
import com.example.qingxue.data.DailyQuoteEntity
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.data.StudyTaskType
import com.example.qingxue.focus.FocusTimerState
import com.example.qingxue.focus.PomodoroPhase
import com.example.qingxue.music.MusicController
import com.example.qingxue.music.MusicState
import com.example.qingxue.rating.FormRatingSummary
import com.example.qingxue.ui.theme.AppAccent
import com.example.qingxue.util.fullDateLabel
import com.example.qingxue.util.shortDateLabel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale
import kotlin.math.abs

private enum class Screen(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home("首页", Icons.Filled.Home),
    Tasks("任务", Icons.AutoMirrored.Filled.List),
    Focus("专注", Icons.Filled.PlayArrow),
    Stats("统计", Icons.Filled.DateRange)
}

@Suppress("DEPRECATION")
private fun setSystemBarsHidden(activity: Activity?, hidden: Boolean) {
    val window = activity?.window ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.let { controller ->
            if (hidden) {
                controller.hide(AndroidWindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                controller.show(AndroidWindowInsets.Type.systemBars())
            }
        }
    } else {
        window.decorView.systemUiVisibility = if (hidden) {
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            View.SYSTEM_UI_FLAG_VISIBLE
        }
    }
}

private enum class TaskKind(val label: String) {
    OneTime("一次任务"),
    Habit("长期习惯")
}

private sealed interface DetailDestination {
    data object Form : DetailDestination
    data object Archive : DetailDestination
    data object FocusHistory : DetailDestination
    data class Task(val taskId: Long) : DetailDestination
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QingXueAppScreen(
    viewModel: QingXueViewModel,
    selectedAccent: AppAccent,
    onAccentSelected: (AppAccent) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.dashboardState.collectAsStateWithLifecycle()
    val history by viewModel.historyState.collectAsStateWithLifecycle()
    val focusTimerState by viewModel.focusTimerState.collectAsStateWithLifecycle()
    val pendingSettlement by viewModel.pendingFocusSettlement.collectAsStateWithLifecycle()
    val aiAnalysis by viewModel.aiAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val analysisError by viewModel.analysisError.collectAsStateWithLifecycle()
    val backupMessage by viewModel.backupMessage.collectAsStateWithLifecycle()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    var selectedTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var detailDestination by remember { mutableStateOf<DetailDestination?>(null) }
    var editingTask by remember { mutableStateOf<StudyTaskEntity?>(null) }
    var pendingTaskHabitId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingCountdown by remember { mutableStateOf<CountdownEventEntity?>(null) }
    var showAppSettings by rememberSaveable { mutableStateOf(false) }
    var showRoundPicker by rememberSaveable { mutableStateOf(false) }
    var apiKeyInput by rememberSaveable { mutableStateOf(ApiKeyManager.getApiKey(context)) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it, selectedAccent.storageKey) }
    }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.importBackup(it) { restoredAccent ->
                onAccentSelected(AppAccent.fromStorage(restoredAccent))
            }
        }
    }

    LaunchedEffect(backupMessage) {
        backupMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearBackupMessage()
        }
    }
    LaunchedEffect(focusTimerState.activeTaskId, focusTimerState.activeHabitId) {
        (focusTimerState.activeTaskId ?: focusTimerState.activeHabitId)?.let {
            selectedTaskId = it
        }
    }

    val immersiveFocus =
        detailDestination == null && currentScreen == Screen.Focus && focusTimerState.hasStarted

    DisposableEffect(immersiveFocus, context) {
        val activity = context as? Activity
        setSystemBarsHidden(activity, immersiveFocus)
        onDispose {
            if (immersiveFocus) setSystemBarsHidden(activity, false)
        }
    }

    BackHandler(enabled = immersiveFocus) {
        currentScreen = Screen.Home
    }

    BackHandler(enabled = detailDestination != null) {
        detailDestination = null
    }

    Scaffold(
        topBar = {
            if (!immersiveFocus) {
                TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (detailDestination == null) {
                            BrandMark(Modifier.size(30.dp))
                            Spacer(Modifier.width(10.dp))
                        }
                        Text(
                            text = when (detailDestination) {
                                DetailDestination.Form -> "FORM 详情"
                                DetailDestination.Archive -> "归档任务"
                                DetailDestination.FocusHistory -> "Match History"
                                is DetailDestination.Task -> {
                                    val destination = detailDestination as DetailDestination.Task
                                    val selected = history.tasks.firstOrNull {
                                        it.id == destination.taskId
                                    }
                                    if (selected?.isHabit == true) "习惯详情" else "任务详情"
                                }
                                null -> currentScreen.label
                            },
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    if (detailDestination != null) {
                        IconButton(onClick = { detailDestination = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
                actions = {
                    if (detailDestination == null) {
                        IconButton(onClick = { showAppSettings = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "外观与通知设置")
                        }
                    }
                }
                )
            }
        },
        bottomBar = {
            if (!immersiveFocus && detailDestination == null) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
                    Screen.entries.forEach { screen ->
                        NavigationBarItem(
                            selected = currentScreen == screen,
                            onClick = { currentScreen = screen },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label
                                )
                            },
                            label = { Text(screen.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = detailDestination to currentScreen,
                transitionSpec = {
                    val openingDetail = initialState.first == null && targetState.first != null
                    val closingDetail = initialState.first != null && targetState.first == null
                    when {
                        openingDetail -> {
                            (slideInHorizontally(tween(220)) { width -> width / 4 } +
                                fadeIn(tween(160))) togetherWith
                                (slideOutHorizontally(tween(220)) { width -> -width / 8 } +
                                    fadeOut(tween(120)))
                        }
                        closingDetail -> {
                            (slideInHorizontally(tween(220)) { width -> -width / 8 } +
                                fadeIn(tween(160))) togetherWith
                                (slideOutHorizontally(tween(220)) { width -> width / 4 } +
                                    fadeOut(tween(120)))
                        }
                        else -> fadeIn(tween(160)) togetherWith fadeOut(tween(100))
                    }
                },
                label = "main-screen"
            ) { (detail, screen) ->
                when (detail) {
                    DetailDestination.Form -> FormDetailsScreen(
                        summary = state.formRating,
                        aiAnalysis = aiAnalysis,
                        isAnalyzing = isAnalyzing,
                        analysisError = analysisError,
                        onRequestAnalysis = viewModel::requestAiAnalysis
                    )
                    DetailDestination.Archive -> ArchiveScreen(
                        tasks = history.tasks.filter { it.isArchived },
                        onRestore = viewModel::restoreTask
                    )
                    DetailDestination.FocusHistory -> FocusHistoryScreen(
                        history = history,
                        onSaveReflection = viewModel::saveReflection
                    )
                    is DetailDestination.Task -> {
                        val task = history.tasks.firstOrNull { it.id == detail.taskId }
                        if (task == null) {
                            EmptyCard("这个任务已不存在。")
                        } else {
                            TaskDetailScreen(
                                task = task,
                                allTasks = history.tasks,
                                sessions = history.sessions,
                                onEdit = { editingTask = task },
                                onStartFocus = {
                                    selectedTaskId = task.id
                                    detailDestination = null
                                    currentScreen = Screen.Focus
                                },
                                onAddTodayTask = {
                                    pendingTaskHabitId = task.id
                                    detailDestination = null
                                    currentScreen = Screen.Tasks
                                },
                                onOpenTask = { taskId ->
                                    detailDestination = DetailDestination.Task(taskId)
                                }
                            )
                        }
                    }
                    null -> when (screen) {
                        Screen.Home -> HomeScreen(
                            state = state,
                            onOpenFormDetails = {
                                detailDestination = DetailDestination.Form
                            },
                            onChooseDailyMatch = { showRoundPicker = true },
                            onStartFocus = { requestedTaskId ->
                                selectedTaskId = if (
                                    focusTimerState.isRunning ||
                                    focusTimerState.activeTaskId != null ||
                                    focusTimerState.activeHabitId != null
                                ) {
                                    focusTimerState.activeTaskId ?: focusTimerState.activeHabitId
                                } else {
                                    requestedTaskId
                                        ?: state.dailyMatch?.mainTaskId
                                        ?: state.todayTasks.firstOrNull { !it.completed }?.id
                                }
                                currentScreen = Screen.Focus
                            },
                            onToggleTask = viewModel::toggleTask,
                            onEditTask = {
                                detailDestination = DetailDestination.Task(it.id)
                            },
                            onAddCountdown = viewModel::addCountdownEvent,
                            onEditCountdown = { editingCountdown = it },
                            onToggleCountdownPinned = viewModel::toggleCountdownPinned,
                            onDeleteCountdown = viewModel::deleteCountdownEvent
                        )
                        Screen.Tasks -> TasksScreen(
                            tasks = state.todayTasks,
                            initialHabitId = pendingTaskHabitId,
                            onInitialHabitConsumed = { pendingTaskHabitId = null },
                            onAddTask = viewModel::addTask,
                            onToggleTask = viewModel::toggleTask,
                            onEditTask = {
                                detailDestination = DetailDestination.Task(it.id)
                            },
                            onDeleteTask = viewModel::deleteTask
                        )
                        Screen.Focus -> FocusScreen(
                            tasks = state.todayTasks,
                            suggestedObjective = state.dailyMatch?.manualObjective.orEmpty(),
                            selectedTaskId = selectedTaskId,
                            onSelectTask = { selectedTaskId = it },
                            timerState = focusTimerState,
                            onSetConfig = viewModel::setPomodoroConfig,
                            onStart = { focusMinutes, breakMinutes, cycles, winCondition ->
                                viewModel.setPomodoroConfig(focusMinutes, breakMinutes, cycles)
                                viewModel.startFocusTimer(selectedTaskId, winCondition)
                            },
                            onPause = viewModel::pauseFocusTimer,
                            onEnd = viewModel::endFocusTimer,
                            onLeaveImmersive = { currentScreen = Screen.Home }
                        )
                        Screen.Stats -> StatsScreen(
                            state = state,
                            onOpenFormDetails = {
                                detailDestination = DetailDestination.Form
                            },
                            onOpenFocusHistory = {
                                detailDestination = DetailDestination.FocusHistory
                            }
                        )
                    }
                }
            }
        }
    }

    if (showRoundPicker) {
        TodayRoundPickerDialog(
            current = state.dailyMatch,
            tasks = state.todayTasks,
            onDismiss = { showRoundPicker = false },
            onSave = viewModel::setDailyMatch
        )
    }
    if (showAppSettings) {
        AppSettingsDialog(
            selectedAccent = selectedAccent,
            onAccentSelected = onAccentSelected,
            apiKey = apiKeyInput,
            onApiKeyChange = {
                apiKeyInput = it
                ApiKeyManager.saveApiKey(context, it.trim())
            },
            onExportData = {
                exportLauncher.launch("lock-in-backup-${LocalDate.now()}.json")
            },
            onImportData = { importLauncher.launch(arrayOf("application/json", "text/plain")) },
            onOpenArchive = {
                showAppSettings = false
                detailDestination = DetailDestination.Archive
            },
            onOpenNotificationSettings = {
                openMusicAccessSettings(context)
            },
            onDismiss = { showAppSettings = false }
        )
    }

    pendingSettlement?.let { settlement ->
        DemoReviewDialog(
            settlement = settlement,
            onSave = viewModel::settleFocusSession,
            onSkip = { viewModel.skipFocusSettlement("") }
        )
    }

    editingTask?.let { task ->
        TaskEditDialog(
            task = task,
            habits = history.tasks.filter { it.isHabit && !it.isArchived && it.id != task.id },
            onDismiss = { editingTask = null },
            onSave = { updated ->
                viewModel.updateTask(updated)
                editingTask = null
            }
        )
    }

    editingCountdown?.let { event ->
        CountdownEditDialog(
            event = event,
            onDismiss = { editingCountdown = null },
            onSave = { updated ->
                viewModel.updateCountdownEvent(updated)
                editingCountdown = null
            }
        )
    }
}

@Composable
private fun FormInsightCard(summary: FormRatingSummary) {
    val dimensions = listOf(
        "任务执行" to summary.execution,
        "专注过程" to summary.focus,
        "稳定性" to summary.consistency,
        "核心影响" to summary.impact
    )
    val strongest = dimensions.maxBy { it.second }
    val weakest = dimensions.minBy { it.second }
    val tied = strongest.second - weakest.second < 0.001f
    val advice = if (tied) "完成一次绑定任务的专注，FORM 会随着真实记录逐步拉开差异。" else when (weakest.first) {
        "任务执行" -> "下一次只安排一个可以明确完成的最小任务。"
        "专注过程" -> "把下一局目标缩小，并尽量完整跑完计划轮次。"
        "稳定性" -> "固定一个开始时间，先保持连续学习日不断档。"
        else -> "把下一段专注绑定到核心任务，减少低优先级占用。"
    }
    ColumnCard {
        Text("状态解读", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Text(
            if (tied) "各维度暂时持平 · ${(strongest.second * 100).toInt()} 分" else "当前强项：${strongest.first} · ${(strongest.second * 100).toInt()} 分",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (tied) "还没有足够差异来判断主要短板" else "主要短板：${weakest.first} · ${(weakest.second * 100).toInt()} 分",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = advice,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
            lineHeight = 21.sp
        )
    }
}

private fun openMusicAccessSettings(context: Context) {
    val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
        .onFailure {
            val fallback = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(fallback)
        }
}
@Composable
private fun AppSettingsDialog(
    selectedAccent: AppAccent,
    onAccentSelected: (AppAccent) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    onExportData: () -> Unit,
    onImportData: () -> Unit,
    onOpenArchive: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "主题色",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(10.dp))
                AppAccent.entries.chunked(2).forEachIndexed { index, rowAccents ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowAccents.forEach { accent ->
                            ThemeAccentOption(
                                accent = accent,
                                selected = accent == selectedAccent,
                                onClick = { onAccentSelected(accent) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (index < AppAccent.entries.lastIndex / 2) {
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "深浅模式跟随系统",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "AI 复盘",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onApiKeyChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("DeepSeek API Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Text(
                    text = "密钥仅保存在本机，用于生成 FORM 学习复盘。",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(Modifier.height(18.dp))
                Text(
                    text = "数据",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onExportData,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("备份")
                    }
                    OutlinedButton(
                        onClick = onImportData,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.FileUpload, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("恢复")
                    }
                }
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onOpenArchive,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Archive, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("归档任务")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onOpenNotificationSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("音乐权限设置")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        }
    )
}
@Composable
private fun ThemeAccentOption(
    accent: AppAccent,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color.Transparent
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(accent.previewColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = accent.label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: DashboardState,
    onOpenFormDetails: () -> Unit,
    onChooseDailyMatch: () -> Unit,
    onStartFocus: (Long?) -> Unit,
    onToggleTask: (StudyTaskEntity) -> Unit,
    onEditTask: (StudyTaskEntity) -> Unit,
    onAddCountdown: (String, String, String, Boolean) -> Unit,
    onEditCountdown: (CountdownEventEntity) -> Unit,
    onToggleCountdownPinned: (CountdownEventEntity) -> Unit,
    onDeleteCountdown: (CountdownEventEntity) -> Unit
) {
    val context = LocalContext.current
    var showCountdownForm by rememberSaveable { mutableStateOf(false) }
    var eventTitle by rememberSaveable { mutableStateOf("") }
    var eventDescription by rememberSaveable { mutableStateOf("") }
    var eventDate by rememberSaveable {
        mutableStateOf(LocalDate.now().plusDays(30).toString())
    }
    var eventPinned by rememberSaveable { mutableStateOf(false) }

    val orderedTasks = state.todayTasks.sortedWith(
        compareBy<StudyTaskEntity> { it.completed }
            .thenByDescending { it.isCore }
            .thenByDescending { it.isHabit }
            .thenByDescending { it.createdAt }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TodayRoundCard(
                match = state.dailyMatch,
                tasks = state.todayTasks,
                sessions = state.todaySessions,
                onChoose = onChooseDailyMatch,
                onStart = onStartFocus
            )
        }
        item { ComebackReminderCard() }

        item { SectionTitle("今日任务") }
        if (orderedTasks.isEmpty()) {
            item { EmptyCard("今天还没有任务，去任务页添加一个可以完成的小目标。") }
        } else {
            items(orderedTasks, key = { "home-task-${it.id}" }) { task ->
                TaskRow(
                    task = task,
                    onClick = { onEditTask(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = null
                )
            }
        }

        item {
            SectionTitleWithAction(
                text = "重要日",
                expanded = showCountdownForm,
                onClick = { showCountdownForm = !showCountdownForm }
            )
        }
        item {
            AnimatedVisibility(
                visible = showCountdownForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                CountdownForm(
                    title = eventTitle,
                    description = eventDescription,
                    targetDate = eventDate,
                    isPinned = eventPinned,
                    onTitleChange = { eventTitle = it },
                    onDescriptionChange = { eventDescription = it },
                    onPinnedChange = { eventPinned = it },
                    onPickDate = {
                        showCountdownDatePicker(context, eventDate) { eventDate = it }
                    },
                    onAdd = {
                        onAddCountdown(eventTitle, eventDate, eventDescription, eventPinned)
                        eventTitle = ""
                        eventDescription = ""
                        eventDate = LocalDate.now().plusDays(30).toString()
                        eventPinned = false
                        showCountdownForm = false
                    }
                )
            }
        }
        if (state.countdowns.isEmpty()) {
            item { EmptyCard("添加考试或截止日期，让重要目标始终在眼前。") }
        } else {
            item {
                CountdownHero(
                    item = state.countdowns.first(),
                    onClick = { onEditCountdown(state.countdowns.first().event) },
                    onTogglePinned = { onToggleCountdownPinned(state.countdowns.first().event) },
                    onDelete = { onDeleteCountdown(state.countdowns.first().event) }
                )
            }
            items(state.countdowns.drop(1), key = { "countdown-${it.event.id}" }) { item ->
                CountdownRow(
                    item = item,
                    onClick = { onEditCountdown(item.event) },
                    onTogglePinned = { onToggleCountdownPinned(item.event) },
                    onDelete = { onDeleteCountdown(item.event) }
                )
            }
        }

        state.dailyQuote?.let { quote -> item { DailyQuoteLine(quote) } }
    }
}
@Composable
private fun SectionTitleWithAction(
    text: String,
    expanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().smoothCardClick(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                contentDescription = if (expanded) "收起$text" else "添加$text"
            )
        }
    }
}

@Composable
private fun CountdownForm(
    title: String,
    description: String,
    targetDate: String,
    isPinned: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPinnedChange: (Boolean) -> Unit,
    onPickDate: () -> Unit,
    onAdd: () -> Unit
) {
    ColumnCard {
        Text("添加重要日", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { onTitleChange(it.take(30)) },
            label = { Text("事件名称") },
            placeholder = { Text("例如：研究生考试") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { onDescriptionChange(it.take(160)) },
            label = { Text("描述") },
            placeholder = { Text("范围、地点或你想提醒自己的内容") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 3
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = isPinned,
                onCheckedChange = onPinnedChange
            )
            Text("设为重要并置顶")
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onPickDate, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(fullDateLabel(targetDate), maxLines = 1)
            }
            Button(onClick = onAdd, enabled = title.isNotBlank()) {
                Text("添加")
            }
        }
    }
}

@Composable
private fun CountdownHero(
    item: CountdownItem,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit
) {
    val event = item.event
    val days = item.daysRemaining
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(start = 18.dp, top = 14.dp, end = 8.dp, bottom = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onTogglePinned, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = if (event.isPinned) "取消置顶${event.title}" else "置顶${event.title}",
                        tint = if (event.isPinned) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f)
                        }
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "删除${event.title}")
                }
            }
            Text(
                text = fullDateLabel(event.targetDate),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                fontSize = 13.sp
            )
            if (event.description.isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                Text(
                    text = event.description,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.76f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(16.dp))
            when {
                days > 0 -> {
                    Text(
                        text = "还有",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f),
                        fontSize = 13.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = days.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 52.sp,
                            lineHeight = 54.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "天",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 5.dp, bottom = 7.dp)
                        )
                    }
                }
                days == 0L -> Text(
                    text = "就是今天",
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                else -> Text(
                    text = "已过去 ${abs(days)} 天",
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Composable
private fun CountdownRow(
    item: CountdownItem,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit
) {
    val event = item.event
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 9.dp, bottom = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (event.description.isBlank()) {
                        fullDateLabel(event.targetDate)
                    } else {
                        "${fullDateLabel(event.targetDate)} · ${event.description}"
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = compactCountdownText(item.daysRemaining),
                fontWeight = FontWeight.Bold,
                color = if (item.daysRemaining >= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                },
                modifier = Modifier.padding(start = 8.dp)
            )
            IconButton(onClick = onTogglePinned, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = if (event.isPinned) "取消置顶${event.title}" else "置顶${event.title}",
                    tint = if (event.isPinned) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
                    }
                )
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Filled.Delete, contentDescription = "删除${event.title}")
            }
        }
    }
}

@Composable
private fun DailyQuoteLine(quote: DailyQuoteEntity) {
    val source = if (quote.source in setOf("轻学", "练度", "LOCK IN")) "" else "  ·  ${quote.source}"
    Text(
        text = "“${quote.text}”$source",
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 18.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f),
        fontSize = 12.sp,
        lineHeight = 18.sp,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

private fun compactCountdownText(days: Long): String {
    return when {
        days > 0 -> "剩 $days 天"
        days == 0L -> "今天"
        else -> "已过 ${abs(days)} 天"
    }
}

private fun showCountdownDatePicker(
    context: Context,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val today = LocalDate.now()
    val selected = runCatching { LocalDate.parse(selectedDate) }.getOrDefault(today)
    DatePickerDialog(
        context,
        { _, year, month, day ->
            onDateSelected(LocalDate.of(year, month + 1, day).toString())
        },
        selected.year,
        selected.monthValue - 1,
        selected.dayOfMonth
    ).apply {
        datePicker.minDate = today
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.show()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(
    tasks: List<StudyTaskEntity>,
    initialHabitId: Long?,
    onInitialHabitConsumed: () -> Unit,
    onAddTask: (String, String, String, Int, Boolean, String, Boolean, Long?) -> Unit,
    onToggleTask: (StudyTaskEntity) -> Unit,
    onEditTask: (StudyTaskEntity) -> Unit,
    onDeleteTask: (StudyTaskEntity) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("45") }
    var taskKind by rememberSaveable { mutableStateOf(TaskKind.OneTime) }
    var studyType by rememberSaveable { mutableStateOf(StudyTaskType.General) }
    var isCore by rememberSaveable { mutableStateOf(false) }
    var selectedHabitId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showTaskForm by rememberSaveable { mutableStateOf(false) }
    val habits = tasks.filter { it.isHabit }
    val oneTimeTasks = tasks.filterNot { it.isHabit }

    LaunchedEffect(initialHabitId, habits) {
        if (initialHabitId != null && habits.any { it.id == initialHabitId }) {
            taskKind = TaskKind.OneTime
            selectedHabitId = initialHabitId
            showTaskForm = true
            onInitialHabitConsumed()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionTitleWithAction(
                text = "任务",
                expanded = showTaskForm,
                onClick = { showTaskForm = !showTaskForm }
            )
        }
        item {
            AnimatedVisibility(
                visible = showTaskForm,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ColumnCard {
                    Text("新增任务", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        TaskKind.entries.forEachIndexed { index, kind ->
                            SegmentedButton(
                                selected = taskKind == kind,
                                onClick = {
                                    taskKind = kind
                                    if (kind == TaskKind.Habit) selectedHabitId = null
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = TaskKind.entries.size
                                )
                            ) {
                                Text(kind.label)
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("学习类型", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    StudyTypePicker(
                        selected = studyType,
                        onSelected = { studyType = it }
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = isCore, onCheckedChange = { isCore = it })
                        Text("核心任务")
                    }
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.take(60) },
                        label = { Text(if (taskKind == TaskKind.Habit) "习惯名称" else "任务名称") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.take(240) },
                        label = { Text("描述") },
                        placeholder = { Text("目标、范围或完成标准") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it.take(30) },
                            label = { Text("科目") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minutes,
                            onValueChange = { minutes = it.filter(Char::isDigit).take(3) },
                            label = {
                                Text(
                                    if (taskKind == TaskKind.Habit) "每日目标" else "预计分钟"
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    if (taskKind == TaskKind.OneTime && habits.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text("所属习惯（可选）", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        HabitPicker(
                            habits = habits,
                            selectedHabitId = selectedHabitId,
                            onSelected = { selectedHabitId = it }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = title.isNotBlank(),
                        onClick = {
                            onAddTask(
                                title,
                                subject,
                                description,
                                minutes.toIntOrNull() ?: 45,
                                taskKind == TaskKind.Habit,
                                studyType.storageValue,
                                isCore,
                                selectedHabitId.takeIf { taskKind == TaskKind.OneTime }
                            )
                            title = ""
                            subject = ""
                            description = ""
                            minutes = "45"
                            studyType = StudyTaskType.General
                            isCore = false
                            selectedHabitId = null
                            showTaskForm = false
                        }
                    ) {
                        Text("添加")
                    }
                }
            }
        }
        item { SectionTitle("长期习惯") }
        if (habits.isEmpty()) {
            item { EmptyCard("还没有长期习惯。") }
        } else {
            items(habits, key = { "habit-" + it.id }) { task ->
                TaskRow(
                    task = task,
                    onClick = { onEditTask(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }
        item { SectionTitle("今日一次任务") }
        if (oneTimeTasks.isEmpty()) {
            item { EmptyCard("今天还没有一次任务。") }
        } else {
            items(oneTimeTasks, key = { "task-" + it.id }) { task ->
                TaskRow(
                    task = task,
                    onClick = { onEditTask(task) },
                    onToggle = { onToggleTask(task) },
                    onDelete = { onDeleteTask(task) }
                )
            }
        }
    }
}
@Composable
private fun StudyTypePicker(
    selected: StudyTaskType,
    onSelected: (StudyTaskType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        StudyTaskType.entries.chunked(2).forEach { rowTypes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTypes.forEach { type ->
                    FilterChip(
                        selected = selected == type,
                        onClick = { onSelected(type) },
                        label = {
                            Text(
                                text = type.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitPicker(
    habits: List<StudyTaskEntity>,
    selectedHabitId: Long?,
    onSelected: (Long?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedHabitId == null,
            onClick = { onSelected(null) },
            label = { Text("不关联") }
        )
        habits.forEach { habit ->
            FilterChip(
                selected = selectedHabitId == habit.id,
                onClick = { onSelected(habit.id) },
                label = { Text(habit.title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}
@Composable
private fun FocusScreen(
    tasks: List<StudyTaskEntity>,
    suggestedObjective: String,
    selectedTaskId: Long?,
    onSelectTask: (Long?) -> Unit,
    timerState: FocusTimerState,
    onSetConfig: (Int, Int, Int) -> Unit,
    onStart: (Int, Int, Int, String) -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
    onLeaveImmersive: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val musicController = remember(context) {
        (context.applicationContext as QingXueApp).musicController
    }
    val musicState by musicController.state.collectAsStateWithLifecycle()

    DisposableEffect(lifecycleOwner, musicController) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) musicController.refresh()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var focusInput by rememberSaveable { mutableStateOf(timerState.focusMinutes.toString()) }
    var breakInput by rememberSaveable { mutableStateOf(timerState.breakMinutes.toString()) }
    var cyclesInput by rememberSaveable { mutableStateOf(timerState.totalCycles.toString()) }
    var showFocusSetup by rememberSaveable { mutableStateOf(false) }
    var showPreRound by rememberSaveable { mutableStateOf(false) }
    var requestedWinCondition by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(
        timerState.focusMinutes,
        timerState.breakMinutes,
        timerState.totalCycles,
        timerState.hasStarted
    ) {
        if (!timerState.hasStarted) {
            focusInput = timerState.focusMinutes.toString()
            breakInput = timerState.breakMinutes.toString()
            cyclesInput = timerState.totalCycles.toString()
        }
    }

    val remainingSeconds = timerState.remainingSeconds(now)
    val focusValue = focusInput.validInt(1..120)
    val breakValue = breakInput.validInt(1..60)
    val cyclesValue = cyclesInput.validInt(1..8)
    val configValid = focusValue != null && breakValue != null && cyclesValue != null
    val selectedTaskTitle = tasks.firstOrNull { it.id == selectedTaskId }?.title
        ?: suggestedObjective.takeIf { it.isNotBlank() }
        ?: "自由专注"
    val executeStart: (String) -> Unit = { condition ->
        if (configValid) {
            onStart(
                checkNotNull(focusValue),
                checkNotNull(breakValue),
                checkNotNull(cyclesValue),
                condition
            )
        }
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) executeStart(requestedWinCondition)
    }
    val requestStart: (String) -> Unit = { condition ->
        requestedWinCondition = condition
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            executeStart(condition)
        }
    }
    val toggleTimer = {
        when {
            timerState.isRunning -> onPause()
            timerState.hasStarted -> requestStart(timerState.winCondition)
            else -> showPreRound = true
        }
    }

    LaunchedEffect(timerState.isRunning, timerState.endsAt) {
        now = System.currentTimeMillis()
        while (timerState.isRunning) {
            now = System.currentTimeMillis()
            delay(1000)
        }
    }

    if (timerState.hasStarted) {
        ImmersiveFocusContent(
            timerState = timerState,
            remainingSeconds = remainingSeconds,
            taskTitle = selectedTaskTitle,
            onToggle = toggleTimer,
            onEnd = onEnd,
            onLeave = onLeaveImmersive
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                val isFocusPhase = timerState.phase == PomodoroPhase.Focus
                val panelColor = if (isFocusPhase) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
                val panelContent = if (isFocusPhase) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
                val indicatorColor = MaterialTheme.colorScheme.primary
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = panelColor,
                        contentColor = panelContent
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().classicCamoPattern(0.7f).padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedTaskTitle,
                            color = panelContent.copy(alpha = 0.70f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "第 ${timerState.currentCycle}/${timerState.totalCycles} 轮 · " +
                                timerState.phase.label,
                            fontWeight = FontWeight.SemiBold,
                            color = indicatorColor
                        )
                        Spacer(Modifier.height(14.dp))
                        TimerDial(
                            remainingSeconds = remainingSeconds,
                            totalSeconds = timerState.phaseTotalSeconds,
                            indicatorColor = indicatorColor,
                            trackColor = panelContent.copy(alpha = 0.12f)
                        )
                        Spacer(Modifier.height(12.dp))
                        val status = when {
                            timerState.isRunning -> "${timerState.phase.label}进行中"
                            timerState.hasStarted -> "${timerState.phase.label}已暂停" +
                                if (timerState.pauseCount > 0) " · 专注暂停 ${timerState.pauseCount} 次" else ""
                            else -> "计划专注 ${timerState.focusMinutes * timerState.totalCycles} 分钟"
                        }
                        Text(
                            text = status,
                            color = panelContent.copy(alpha = 0.72f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
            item {
                MusicSection(state = musicState, controller = musicController)
            }
            item { Spacer(Modifier.height(72.dp)) }
        }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = toggleTimer,
                    enabled = configValid,
                    modifier = Modifier.weight(1f).height(56.dp)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Start Round")
                }
                FloatingActionButton(
                    onClick = { showFocusSetup = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Icon(Icons.Filled.Tune, contentDescription = "专注设置")
                }
            }
        }
    }

    if (showPreRound) {
        PreRoundDialog(
            initialWinCondition = selectedTaskTitle,
            onDismiss = { showPreRound = false },
            onAdjust = { showFocusSetup = true },
            onStart = { condition ->
                showPreRound = false
                requestStart(condition)
            }
        )
    }
    if (showFocusSetup) {
        AlertDialog(
            onDismissRequest = { showFocusSetup = false },
            title = { Text("专注设置") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 520.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("当前任务", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    TaskPicker(
                        tasks = tasks,
                        selectedTaskId = selectedTaskId,
                        onSelectTask = onSelectTask,
                        enabled = !timerState.hasStarted
                    )
                    Spacer(Modifier.height(18.dp))
                    Text("番茄设置", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PomodoroConfigField(
                            value = focusInput,
                            label = "专注/分",
                            enabled = !timerState.hasStarted,
                            isError = focusValue == null,
                            modifier = Modifier.weight(1f),
                            onValueChange = { raw ->
                                focusInput = raw.filter(Char::isDigit).take(3)
                                val nextFocus = focusInput.validInt(1..120)
                                val nextBreak = breakInput.validInt(1..60)
                                val nextCycles = cyclesInput.validInt(1..8)
                                if (nextFocus != null && nextBreak != null && nextCycles != null) {
                                    onSetConfig(nextFocus, nextBreak, nextCycles)
                                }
                            }
                        )
                        PomodoroConfigField(
                            value = breakInput,
                            label = "休息/分",
                            enabled = !timerState.hasStarted,
                            isError = breakValue == null,
                            modifier = Modifier.weight(1f),
                            onValueChange = { raw ->
                                breakInput = raw.filter(Char::isDigit).take(2)
                                val nextFocus = focusInput.validInt(1..120)
                                val nextBreak = breakInput.validInt(1..60)
                                val nextCycles = cyclesInput.validInt(1..8)
                                if (nextFocus != null && nextBreak != null && nextCycles != null) {
                                    onSetConfig(nextFocus, nextBreak, nextCycles)
                                }
                            }
                        )
                        PomodoroConfigField(
                            value = cyclesInput,
                            label = "轮次",
                            enabled = !timerState.hasStarted,
                            isError = cyclesValue == null,
                            modifier = Modifier.weight(1f),
                            onValueChange = { raw ->
                                cyclesInput = raw.filter(Char::isDigit).take(1)
                                val nextFocus = focusInput.validInt(1..120)
                                val nextBreak = breakInput.validInt(1..60)
                                val nextCycles = cyclesInput.validInt(1..8)
                                if (nextFocus != null && nextBreak != null && nextCycles != null) {
                                    onSetConfig(nextFocus, nextBreak, nextCycles)
                                }
                            }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (timerState.hasStarted) {
                            "本轮已开始，结束后可修改任务和番茄参数。"
                        } else {
                            "专注 1–120 分钟 · 休息 1–60 分钟 · 1–8 轮"
                        },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFocusSetup = false }) {
                    Text("完成")
                }
            }
        )
    }
}

@Composable
private fun ImmersiveFocusContent(
    timerState: FocusTimerState,
    remainingSeconds: Int,
    taskTitle: String,
    onToggle: () -> Unit,
    onEnd: () -> Unit,
    onLeave: () -> Unit
) {
    val isFocusPhase = timerState.phase == PomodoroPhase.Focus
    val backgroundColor = if (isFocusPhase) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isFocusPhase) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val indicatorColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .classicCamoPattern(if (isFocusPhase) 0.8f else 0.25f)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp)
        ) {
            IconButton(
                onClick = onLeave,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 12.dp)
            ) {
                Icon(Icons.Filled.Close, contentDescription = "暂时离开专注界面")
            }
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 72.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = taskTitle,
                    color = contentColor.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "ROUND ${timerState.currentCycle}/${timerState.totalCycles} · ${timerState.phase.label}",
                    color = indicatorColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(28.dp))
                TimerDial(
                    remainingSeconds = remainingSeconds,
                    totalSeconds = timerState.phaseTotalSeconds,
                    indicatorColor = indicatorColor,
                    trackColor = contentColor.copy(alpha = 0.14f)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = if (timerState.isRunning) {
                        "${timerState.phase.label}进行中"
                    } else {
                        "${timerState.phase.label}已暂停" +
                            if (timerState.pauseCount > 0) " · 暂停 ${timerState.pauseCount} 次" else ""
                    },
                    color = contentColor.copy(alpha = 0.72f),
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onToggle) {
                        Icon(
                            if (timerState.isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(if (timerState.isRunning) "Tactical Pause" else "继续回合")
                    }
                    OutlinedButton(onClick = onEnd) {
                        Text("End Round")
                    }
                }
            }
        }
    }
}

@Composable
private fun MusicSection(state: MusicState, controller: MusicController) {
    val context = LocalContext.current
    when {
        state.needsNotificationAccess -> MusicStatusCard(
            title = "连接音乐控制",
            detail = "授权通知使用权后，可控制正在播放的音乐。",
            actionLabel = "去授权",
            onAction = { openMusicAccessSettings(context) }
        )
        state.isConnecting -> MusicStatusCard(
            title = "正在连接音乐",
            detail = "正在读取系统媒体会话，请稍候。"
        )
        state.connectionIssue -> MusicStatusCard(
            title = "音乐连接暂不可用",
            detail = "可重试连接，或重新检查通知使用权。",
            actionLabel = "重试",
            onAction = controller::refresh
        )
        state.isAvailable -> MusicPlayerCard(state = state, controller = controller)
        else -> MusicStatusCard(
            title = "音乐控制",
            detail = "开始播放音乐后，歌曲信息会自动显示在这里。",
            actionLabel = "刷新",
            onAction = controller::refresh
        )
    }
}

@Composable
private fun MusicStatusCard(
    title: String,
    detail: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(
                    detail,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
            if (actionLabel != null && onAction != null) {
                TextButton(onClick = onAction) { Text(actionLabel) }
            }
        }
    }
}

@Composable
private fun MusicPlayerCard(state: MusicState, controller: MusicController) {
    val progress = if (state.duration > 0L) {
        state.position.toFloat() / state.duration.toFloat()
    } else 0f

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val artwork = state.albumArt
                if (artwork != null) {
                    Image(
                        bitmap = artwork.asImageBitmap(),
                        contentDescription = "专辑封面",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(6.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier.size(64.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        state.title.ifBlank { "正在播放" },
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        state.artist.ifBlank { state.album },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            formatMusicTime(state.position),
                            modifier = Modifier.width(44.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                        Box(
                            modifier = Modifier.weight(1f).height(24.dp)
                                .pointerInput(state.duration, state.canSeek) {
                                    if (state.canSeek && state.duration > 0L) {
                                        detectTapGestures { offset ->
                                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                            controller.seekTo((state.duration * fraction).toLong())
                                        }
                                    }
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                Modifier.fillMaxWidth().height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            Box(
                                Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Text(
                            formatMusicTime(state.duration),
                            modifier = Modifier.width(44.dp),
                            textAlign = TextAlign.End,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = controller::skipToPrevious, enabled = state.canSkipPrevious) {
                    Icon(Icons.Filled.SkipPrevious, contentDescription = "上一首")
                }
                Spacer(Modifier.width(18.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    IconButton(onClick = controller::playPause, enabled = state.canPlayPause) {
                        Icon(
                            if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.isPlaying) "暂停" else "播放"
                        )
                    }
                }
                Spacer(Modifier.width(18.dp))
                IconButton(onClick = controller::skipToNext, enabled = state.canSkipNext) {
                    Icon(Icons.Filled.SkipNext, contentDescription = "下一首")
                }
            }
        }
    }
}

private fun formatMusicTime(milliseconds: Long): String {
    val totalSeconds = (milliseconds.coerceAtLeast(0L) / 1000L)
    return "%d:%02d".format(totalSeconds / 60L, totalSeconds % 60L)
}
@Composable
private fun PomodoroConfigField(
    value: String,
    label: String,
    enabled: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        singleLine = true,
        label = { Text(label, maxLines = 1, fontSize = 12.sp) },
        textStyle = TextStyle(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

private fun String.validInt(range: IntRange): Int? {
    return toIntOrNull()?.takeIf { it in range }
}

@Composable
private fun TaskPicker(
    tasks: List<StudyTaskEntity>,
    selectedTaskId: Long?,
    onSelectTask: (Long?) -> Unit,
    enabled: Boolean
) {
    if (tasks.isEmpty()) {
        Text("未绑定任务，本次只记录专注时长。")
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        tasks.filter { !it.completed }.ifEmpty { tasks }.take(4).forEach { task ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onSelectTask(task.id) },
                enabled = enabled
            ) {
                Text(
                    text = if (selectedTaskId == task.id) "✓ ${task.title}" else task.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TimerDial(
    remainingSeconds: Int,
    totalSeconds: Int,
    indicatorColor: Color,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    val progress = if (totalSeconds == 0) 0f else remainingSeconds.toFloat() / totalSeconds
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                size = Size(size.width - stroke, size.height - stroke),
                topLeft = Offset(stroke / 2, stroke / 2)
            )
            drawArc(
                color = indicatorColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round),
                size = Size(size.width - stroke, size.height - stroke),
                topLeft = Offset(stroke / 2, stroke / 2)
            )
        }
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        Text(
            text = "%02d:%02d".format(minutes, seconds),
            fontSize = 42.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ArchiveScreen(
    tasks: List<StudyTaskEntity>,
    onRestore: (StudyTaskEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "归档不会删除历史专注记录，恢复后任务会重新回到原来的日期或习惯列表。",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.64f),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
        if (tasks.isEmpty()) {
            item { EmptyCard("还没有归档任务。") }
        } else {
            items(tasks, key = { "archive-${it.id}" }) { task ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(task.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Text(
                                if (task.isHabit) "长期习惯" else "${task.subject} · ${task.date}",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                                fontSize = 12.sp
                            )
                        }
                        TextButton(onClick = { onRestore(task) }) { Text("恢复") }
                    }
                }
            }
        }
    }
}
@Composable
private fun StatsScreen(
    state: DashboardState,
    onOpenFormDetails: () -> Unit,
    onOpenFocusHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(
                    label = "连续",
                    value = "${state.streakDays} 天",
                    icon = Icons.Filled.Star,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "今日",
                    value = "${state.todayFocusMinutes} 分钟",
                    icon = Icons.Filled.PlayArrow,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            FormRatingCard(
                summary = state.formRating,
                showDimensions = true,
                onClick = onOpenFormDetails
            )
        }
        item {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.fillMaxWidth().smoothCardClick(onOpenFocusHistory)
            ) {
                Box(Modifier.fillMaxWidth().classicCamoPattern(0.65f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    BrandMark(Modifier.size(36.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Match History", fontWeight = FontWeight.SemiBold)
                        Text(
                            "查看每一局、累计时长与任务筛选",
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.68f),
                            fontSize = 13.sp
                        )
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "查看 Match History"
                    )
                }
            }
        }
        }
        if (state.habitStats.isNotEmpty()) {
            item { SectionTitle("习惯累计") }
            items(state.habitStats, key = { "habit-stat-${it.task.id}" }) { stat ->
                HabitStatRow(stat)
            }
        }
        item { SectionTitle("最近 7 天") }
        item { WeeklyBarChart(state.recentStats) }
    }
}

@Composable
private fun WeeklyBarChart(stats: List<DayStat>) {
    val maxMinutes = stats.maxOfOrNull { it.focusMinutes }?.coerceAtLeast(30) ?: 30
    val totalMinutes = stats.sumOf { it.focusMinutes }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "专注分布",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "共 $totalMinutes 分钟",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                stats.forEach { stat ->
                    val ratio = stat.focusMinutes.toFloat() / maxMinutes
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stat.focusMinutes.toString(),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier.height(96.dp).fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                Modifier
                                    .width(20.dp)
                                    .height(if (stat.focusMinutes == 0) 6.dp else (16 + 80 * ratio).dp)
                                    .background(
                                        color = if (stat.focusMinutes == 0) {
                                            MaterialTheme.colorScheme.surfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.primary
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = shortDateLabel(stat.date),
                            fontSize = 10.sp,
                            lineHeight = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormDetailsScreen(
    summary: FormRatingSummary,
    aiAnalysis: AiAnalysisEntity?,
    isAnalyzing: Boolean,
    analysisError: String?,
    onRequestAnalysis: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ColumnCard {
                Text(
                    text = if (summary.isCalibrating) "当前状态" else "近 7 天 FORM",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 13.sp
                )
                Text(
                    text = summary.rating?.let { String.format(Locale.US, "%.2f", it) }
                        ?: "校准中",
                    fontSize = if (summary.rating == null) 30.sp else 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Text(
                    text = "可信度 ${summary.confidence.label} · " +
                        "${summary.activeDays} 个有效学习日 · ${summary.evidenceCount} 条记录",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                    fontSize = 13.sp
                )
                if (summary.isCalibrating) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { summary.calibrationProgress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
        item { SectionTitle("评分构成") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DetailedRatingDimensionRow("任务执行", "35%", summary.execution)
                DetailedRatingDimensionRow("专注过程", "25%", summary.focus)
                DetailedRatingDimensionRow("稳定性", "25%", summary.consistency)
                DetailedRatingDimensionRow("核心影响", "15%", summary.impact)
            }
        }
        item { FormInsightCard(summary) }
        item {
            ColumnCard {
                Text(
                    text = "AI 赛后复盘",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(10.dp))
                when {
                    isAnalyzing -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text("正在分析最近的学习表现…")
                        }
                    }
                    aiAnalysis != null -> {
                        Text(
                            text = aiAnalysis.overallComment,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(12.dp))
                        val dimensionNotes = remember(aiAnalysis.id, aiAnalysis.dimensionAnalysis) {
                            AiAnalysisResult.fromEntity(aiAnalysis).let { result ->
                                listOf(
                                    "任务执行" to result.executionNote,
                                    "专注过程" to result.focusNote,
                                    "稳定性" to result.consistencyNote,
                                    "核心影响" to result.impactNote
                                ).filter { (_, note) -> note.isNotBlank() }
                            }
                        }
                        dimensionNotes.forEach { (label, note) ->
                            Text(
                                text = label,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = note,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "下一步",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = aiAnalysis.advice,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f),
                            fontSize = 14.sp
                        )
                    }
                    else -> Text(
                        text = "结合 FORM、任务完成和专注记录，生成一份简洁的学习复盘。",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f),
                        fontSize = 14.sp
                    )
                }
                if (!analysisError.isNullOrBlank()) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = analysisError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onRequestAnalysis,
                    enabled = !isAnalyzing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (aiAnalysis == null) "生成分析" else "重新分析")
                }
            }
        }
        if (summary.isCalibrating) {
            item {
                Text(
                    text = "分项为当前样本的暂定表现；累计 3 个有效学习日且达到 4 条记录后显示整体 FORM。",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.66f),
                    fontSize = 13.sp
                )
            }
        }
    }
}
@Composable
private fun DetailedRatingDimensionRow(label: String, weight: String, score: Float) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            Text(
                text = "权重 $weight · ${(score * 100).toInt()} 分",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(9.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun FormRatingCard(
    summary: FormRatingSummary,
    showDimensions: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth().smoothCardClick(onClick)
    ) {
        Box(Modifier.fillMaxWidth().classicCamoPattern(0.65f)) {
            Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("FORM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = if (summary.isCalibrating) {
                            when {
                                summary.daysUntilReady > 0 ->
                                    "已记录 ${summary.activeDays}/3 个有效学习日"
                                summary.evidenceUntilReady > 0 ->
                                    "再积累 ${summary.evidenceUntilReady} 条学习记录"
                                else -> "正在校准近期状态"
                            }
                        } else {
                            "近 7 天 · 可信度 ${summary.confidence.label}"
                        },
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                        fontSize = 13.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = summary.rating?.let { String.format(Locale.US, "%.2f", it) }
                            ?: "校准中",
                        fontWeight = FontWeight.Bold,
                        fontSize = if (summary.rating == null) 20.sp else 32.sp,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "查看 FORM 详情",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (summary.isCalibrating) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { summary.calibrationProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                )
            }

            if (showDimensions && !summary.isCalibrating) {
                Spacer(Modifier.height(14.dp))
                RatingDimensionRow("任务执行", summary.execution)
                RatingDimensionRow("专注过程", summary.focus)
                RatingDimensionRow("稳定性", summary.consistency)
                RatingDimensionRow("核心影响", summary.impact)
            }
        }
    }
}

}
@Composable
private fun RatingDimensionRow(label: String, score: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(76.dp), fontSize = 13.sp)
        LinearProgressIndicator(
            progress = { score.coerceIn(0f, 1f) },
            modifier = Modifier.weight(1f).height(8.dp),
            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
        )
        Text(
            text = "${(score * 100).toInt()}",
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditDialog(
    task: StudyTaskEntity,
    habits: List<StudyTaskEntity>,
    onDismiss: () -> Unit,
    onSave: (StudyTaskEntity) -> Unit
) {
    var title by remember(task.id) { mutableStateOf(task.title) }
    var subject by remember(task.id) { mutableStateOf(task.subject) }
    var description by remember(task.id) { mutableStateOf(task.description) }
    var minutes by remember(task.id) { mutableStateOf(task.estimatedMinutes.toString()) }
    var taskKind by remember(task.id) {
        mutableStateOf(if (task.isHabit) TaskKind.Habit else TaskKind.OneTime)
    }
    var studyType by remember(task.id) {
        mutableStateOf(StudyTaskType.fromStorage(task.studyType))
    }
    var isCore by remember(task.id) { mutableStateOf(task.isCore) }
    var selectedHabitId by remember(task.id) { mutableStateOf(task.habitId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑任务") },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 560.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        TaskKind.entries.forEachIndexed { index, kind ->
                            SegmentedButton(
                                selected = taskKind == kind,
                                onClick = {
                                    taskKind = kind
                                    if (kind == TaskKind.Habit) selectedHabitId = null
                                },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = TaskKind.entries.size
                                )
                            ) {
                                Text(kind.label)
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.take(60) },
                        label = { Text("任务") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it.take(240) },
                        label = { Text("描述") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it.take(30) },
                            label = { Text("科目") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = minutes,
                            onValueChange = { minutes = it.filter(Char::isDigit).take(3) },
                            label = { Text("分钟") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
                if (taskKind == TaskKind.OneTime && habits.isNotEmpty()) {
                    item {
                        Text("所属习惯（可选）", fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        HabitPicker(
                            habits = habits,
                            selectedHabitId = selectedHabitId,
                            onSelected = { selectedHabitId = it }
                        )
                    }
                }
                item {
                    Text("学习类型", fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    StudyTypePicker(selected = studyType, onSelected = { studyType = it })
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCore, onCheckedChange = { isCore = it })
                        Text("核心任务")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    val isHabit = taskKind == TaskKind.Habit
                    onSave(
                        task.copy(
                            title = title,
                            subject = subject,
                            description = description,
                            estimatedMinutes = minutes.toIntOrNull() ?: task.estimatedMinutes,
                            isHabit = isHabit,
                            lastCompletedDate = if (isHabit) task.lastCompletedDate else null,
                            studyType = studyType.storageValue,
                            isCore = isCore,
                            habitId = selectedHabitId.takeUnless { isHabit }
                        )
                    )
                }
            ) {
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

@Composable
private fun CountdownEditDialog(
    event: CountdownEventEntity,
    onDismiss: () -> Unit,
    onSave: (CountdownEventEntity) -> Unit
) {
    val context = LocalContext.current
    var title by remember(event.id) { mutableStateOf(event.title) }
    var description by remember(event.id) { mutableStateOf(event.description) }
    var targetDate by remember(event.id) { mutableStateOf(event.targetDate) }
    var isPinned by remember(event.id) { mutableStateOf(event.isPinned) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑重要日") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(60) },
                    label = { Text("事件名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(160) },
                    label = { Text("描述") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                OutlinedButton(
                    onClick = {
                        showCountdownDatePicker(context, targetDate) { targetDate = it }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(fullDateLabel(targetDate), maxLines = 1)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Text("设为重要并置顶")
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank(),
                onClick = {
                    onSave(
                        event.copy(
                            title = title,
                            targetDate = targetDate,
                            description = description,
                            isPinned = isPinned
                        )
                    )
                }
            ) {
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

@Composable
private fun HabitStatRow(stat: HabitStat) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stat.task.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${stat.task.subject} · 今日目标 ${stat.task.estimatedMinutes} 分钟",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp
                )
            }
            Text(
                text = formatTotalMinutes(stat.totalMinutes),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1
            )
        }
    }
}

private fun formatTotalMinutes(minutes: Long): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    return when {
        hours == 0L -> "$remainingMinutes 分钟"
        remainingMinutes == 0L -> "$hours 小时"
        else -> "$hours 小时 $remainingMinutes 分钟"
    }
}

@Composable
private fun TaskRow(
    task: StudyTaskEntity,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val view = LocalView.current
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface

        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().smoothCardClick(onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 12.dp, end = 10.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .background(accentColor, RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.width(6.dp))
            Checkbox(
                checked = task.completed,
                onCheckedChange = { completed ->
                    if (completed) {
                        view.performHapticFeedback(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                HapticFeedbackConstants.CONFIRM
                            } else {
                                HapticFeedbackConstants.VIRTUAL_KEY
                            }
                        )
                    }
                    onToggle()
                },
                enabled = !task.isHabit
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.isCore) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "核心任务",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                    }
                    Text(
                        task.title,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                val typeLabel = StudyTaskType.fromStorage(task.studyType).label
                val prefix = if (task.isCore) "核心 · $typeLabel" else typeLabel
                Text(
                    text = if (task.isHabit) {
                        "$prefix · ${task.subject} · 今日目标 ${task.estimatedMinutes} 分钟"
                    } else {
                        "$prefix · ${task.subject} · ${task.estimatedMinutes} 分钟"
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = task.description,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "删除${task.title}")
                }
            }
        }
    }
}

@Composable
private fun Modifier.smoothCardClick(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.982f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "card-press"
    )
    return graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(
        interactionSource = interactionSource,
        indication = LocalIndication.current,
        onClick = onClick
    )
}

@Composable
private fun ColumnCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier
    ) {
        Box(Modifier.classicCamoPattern(0.35f)) {
            Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(contentColor.copy(alpha = 0.11f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, color = contentColor.copy(alpha = 0.68f), fontSize = 13.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 21.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun EmptyCard(text: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
}
