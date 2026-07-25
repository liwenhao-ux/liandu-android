package com.example.qingxue.ui

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.qingxue.data.StudyTaskEntity
import com.example.qingxue.data.StudyTaskType
import com.example.qingxue.focus.FocusTimerState
import com.example.qingxue.focus.PomodoroPhase
import com.example.qingxue.music.MusicController
import com.example.qingxue.music.MusicState
import com.example.qingxue.rating.FormRatingSummary
import com.example.qingxue.ui.theme.AppAccent
import com.example.qingxue.ui.theme.AppVisualStyle
import com.example.qingxue.ui.theme.LocalAppVisualStyle
import com.example.qingxue.util.fullDateLabel
import com.example.qingxue.util.shortDateLabel
import com.example.qingxue.util.studyDate
import kotlin.math.abs
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

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
    selectedVisualStyle: AppVisualStyle,
    onAccentSelected: (AppAccent) -> Unit,
    onVisualStyleSelected: (AppVisualStyle) -> Unit
) {
    val context = LocalContext.current
    val isTacticalStyle = selectedVisualStyle == AppVisualStyle.Tactical
    val state by viewModel.dashboardState.collectAsStateWithLifecycle()
    val history by viewModel.historyState.collectAsStateWithLifecycle()
    val focusTimerState by viewModel.focusTimerState.collectAsStateWithLifecycle()
    val pendingSettlement by viewModel.pendingFocusSettlement.collectAsStateWithLifecycle()
    val aiAnalysis by viewModel.aiAnalysis.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val analysisError by viewModel.analysisError.collectAsStateWithLifecycle()
    val backupMessage by viewModel.backupMessage.collectAsStateWithLifecycle()
    val backupImportPreview by viewModel.backupImportPreview.collectAsStateWithLifecycle()
    var currentScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    var selectedTaskId by rememberSaveable { mutableStateOf<Long?>(null) }
    var detailDestination by remember { mutableStateOf<DetailDestination?>(null) }
    var editingTask by remember { mutableStateOf<StudyTaskEntity?>(null) }
    var pendingTaskHabitId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingCountdown by remember { mutableStateOf<CountdownEventEntity?>(null) }
    var showAppSettings by rememberSaveable { mutableStateOf(false) }
    var showRoundPicker by rememberSaveable { mutableStateOf(false) }
    var apiKeyInput by rememberSaveable { mutableStateOf(ApiKeyManager.getApiKey(context)) }
    var settingsRefreshToken by remember { mutableIntStateOf(0) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(it, selectedAccent.storageKey) }
    }
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let(viewModel::prepareBackupImport)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) settingsRefreshToken++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val notificationReady = remember(settingsRefreshToken, showAppSettings) {
        areAppNotificationsReady(context)
    }
    val musicAccessReady = remember(settingsRefreshToken, showAppSettings) {
        isMusicAccessEnabled(context)
    }
    val batteryReady = remember(settingsRefreshToken, showAppSettings) {
        isBatteryOptimizationIgnored(context)
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
                        if (detailDestination == null && currentScreen == Screen.Home) {
                            BrandMark(Modifier.size(30.dp))
                            Spacer(Modifier.width(10.dp))
                        }
                        Text(
                            text = when (detailDestination) {
                                DetailDestination.Form -> "FORM 详情"
                                DetailDestination.Archive -> "归档任务"
                                DetailDestination.FocusHistory -> if (isTacticalStyle) "Match History" else "专注历史"
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
                    if (detailDestination == null && currentScreen == Screen.Home) {
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
                        onSaveReflection = viewModel::saveReflection,
                        onSaveManualFocus = viewModel::saveManualFocus,
                        onDeleteSession = viewModel::deleteFocusSession
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
                            onChooseDailyMatch = { showRoundPicker = true },
                            onOpenTasks = { currentScreen = Screen.Tasks },
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
                                viewModel.startFocusTimer(
                                    selectedTaskId,
                                    winCondition,
                                    focusMinutes,
                                    breakMinutes,
                                    cycles
                                )
                            },
                            onPhaseElapsed = viewModel::reconcileFocusTimer,
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
            selectedVisualStyle = selectedVisualStyle,
            onAccentSelected = onAccentSelected,
            onVisualStyleSelected = onVisualStyleSelected,
            apiKey = apiKeyInput,
            onApiKeyChange = {
                apiKeyInput = it
                ApiKeyManager.saveApiKey(context, it.trim())
            },
            onExportData = {
                exportLauncher.launch("lock-in-backup-${studyDate()}.json")
            },
            onImportData = {
                showAppSettings = false
                importLauncher.launch(arrayOf("application/json", "text/plain"))
            },
            backupSummary = "${history.tasks.size} 个任务 · ${history.sessions.size} 条专注记录",
            notificationReady = notificationReady,
            musicAccessReady = musicAccessReady,
            batteryReady = batteryReady,
            onOpenArchive = {
                showAppSettings = false
                detailDestination = DetailDestination.Archive
            },
            onOpenNotificationSettings = { openAppNotificationSettings(context) },
            onOpenMusicAccessSettings = { openMusicAccessSettings(context) },
            onOpenBatterySettings = { openBatteryOptimizationSettings(context) },
            onDismiss = { showAppSettings = false }
        )
    }

    backupImportPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = viewModel::cancelBackupImport,
            title = { Text("确认恢复备份？") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("备份内容")
                    Text(
                        "${preview.taskCount} 个任务 · ${preview.sessionCount} 条专注记录 · " +
                            "${preview.countdownCount} 个重要日 · ${preview.matchCount} 天计划",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                        fontSize = 13.sp
                    )
                    Text(
                        "恢复后会替换当前 App 内的数据，建议先导出一次当前备份。",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmBackupImport { restoredAccent ->
                            onAccentSelected(AppAccent.fromStorage(restoredAccent))
                        }
                    }
                ) { Text("确认恢复") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelBackupImport) { Text("取消") }
            }
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

private fun areAppNotificationsReady(context: Context): Boolean {
    val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    val notificationsEnabled = context.getSystemService(NotificationManager::class.java)
        .areNotificationsEnabled()
    return permissionGranted && notificationsEnabled
}

private fun isMusicAccessEnabled(context: Context): Boolean {
    return Settings.Secure.getString(
        context.contentResolver,
        "enabled_notification_listeners"
    ).orEmpty().split(':').mapNotNull(ComponentName::unflattenFromString).any {
        it.packageName == context.packageName
    }
}

private fun isBatteryOptimizationIgnored(context: Context): Boolean {
    val powerManager = context.getSystemService(PowerManager::class.java)
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

private fun openAppNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
        .onFailure { openAppDetailsSettings(context) }
}

private fun openBatteryOptimizationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
        .onFailure { openAppDetailsSettings(context) }
}

private fun openAppDetailsSettings(context: Context) {
    context.startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${context.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
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
    selectedVisualStyle: AppVisualStyle,
    onAccentSelected: (AppAccent) -> Unit,
    onVisualStyleSelected: (AppVisualStyle) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    onExportData: () -> Unit,
    onImportData: () -> Unit,
    backupSummary: String,
    notificationReady: Boolean,
    musicAccessReady: Boolean,
    batteryReady: Boolean,
    onOpenArchive: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenMusicAccessSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "战术风格",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            text = if (selectedVisualStyle == AppVisualStyle.Tactical) {
                                "红黑迷彩与竞技回合文案"
                            } else {
                                "简洁界面，适合日常学习"
                            },
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = selectedVisualStyle == AppVisualStyle.Tactical,
                        onCheckedChange = { enabled ->
                            onVisualStyleSelected(
                                if (enabled) AppVisualStyle.Tactical else AppVisualStyle.Standard
                            )
                            if (enabled) {
                                onAccentSelected(AppAccent.GrayPurple)
                            } else if (selectedAccent == AppAccent.GrayPurple) {
                                onAccentSelected(AppAccent.MistGreen)
                            }
                        }
                    )
                }
                Spacer(Modifier.height(18.dp))
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
                    text = "功能检测",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                CapabilityStatusRow(
                    title = "锁屏与倒计时通知",
                    ready = notificationReady,
                    readyText = "通知权限正常",
                    blockedText = "通知权限未开启",
                    onAction = onOpenNotificationSettings
                )
                CapabilityStatusRow(
                    title = "音乐控制",
                    ready = musicAccessReady,
                    readyText = "音乐访问已授权",
                    blockedText = "需要通知使用权",
                    onAction = onOpenMusicAccessSettings
                )
                CapabilityStatusRow(
                    title = "后台计时",
                    ready = batteryReady,
                    readyText = "电池限制已放宽",
                    blockedText = "系统可能限制后台运行",
                    onAction = onOpenBatterySettings
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
                Spacer(Modifier.height(4.dp))
                Text(
                    backupSummary,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                    fontSize = 12.sp
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
private fun CapabilityStatusRow(
    title: String,
    ready: Boolean,
    readyText: String,
    blockedText: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (ready) Icons.Filled.Check else Icons.Filled.Close,
            contentDescription = null,
            tint = if (ready) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(
                if (ready) readyText else blockedText,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f),
                fontSize = 12.sp
            )
        }
        TextButton(onClick = onAction) { Text("设置") }
    }
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
    onChooseDailyMatch: () -> Unit,
    onOpenTasks: () -> Unit,
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
    var showAllCountdowns by rememberSaveable { mutableStateOf(false) }
    var eventTitle by rememberSaveable { mutableStateOf("") }
    var eventDescription by rememberSaveable { mutableStateOf("") }
    var eventDate by rememberSaveable {
        mutableStateOf(studyDate().plusDays(30).toString())
    }
    var eventPinned by rememberSaveable { mutableStateOf(false) }

    val orderedTasks = state.todayTasks.sortedWith(
        compareBy<StudyTaskEntity> { it.completed }
            .thenByDescending { it.isCore }
            .thenByDescending { it.isHabit }
            .thenByDescending { it.createdAt }
    )
    val incompleteTasks = orderedTasks.filterNot { it.completed }
    val visibleTasks = incompleteTasks.take(3)
    val visibleCountdowns = if (showAllCountdowns) state.countdowns else state.countdowns.take(1)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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

        item {
            SectionTitleWithTextAction(
                text = "今日任务",
                action = "全部",
                onClick = onOpenTasks
            )
        }
        when {
            orderedTasks.isEmpty() -> item {
                EmptyCard("今天还没有任务。")
            }
            visibleTasks.isEmpty() -> item {
                EmptyCard("今天的任务已经全部完成。")
            }
            else -> {
                items(visibleTasks, key = { "home-task-${it.id}" }) { task ->
                    TaskRow(
                        task = task,
                        onClick = { onEditTask(task) },
                        onToggle = { onToggleTask(task) },
                        onDelete = null
                    )
                }
                if (incompleteTasks.size > visibleTasks.size) {
                    item {
                        TextButton(onClick = onOpenTasks, modifier = Modifier.fillMaxWidth()) {
                            Text("还有 ${incompleteTasks.size - visibleTasks.size} 项未完成")
                        }
                    }
                }
            }
        }

        item { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant) }
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
                        eventDate = studyDate().plusDays(30).toString()
                        eventPinned = false
                        showCountdownForm = false
                    }
                )
            }
        }
        if (state.countdowns.isEmpty()) {
            item { EmptyCard("还没有重要日期。") }
        } else {
            items(visibleCountdowns, key = { "countdown-${it.event.id}" }) { item ->
                CountdownRow(
                    item = item,
                    onClick = { onEditCountdown(item.event) },
                    onTogglePinned = { onToggleCountdownPinned(item.event) },
                    onDelete = { onDeleteCountdown(item.event) }
                )
            }
            if (state.countdowns.size > 1) {
                item {
                    TextButton(
                        onClick = { showAllCountdowns = !showAllCountdowns },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (showAllCountdowns) "收起" else "查看全部 ${state.countdowns.size} 个")
                    }
                }
            }
        }
    }
}
@Composable
private fun SectionTitleWithTextAction(
    text: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.weight(1f))
        TextButton(onClick = onClick) {
            Text(action)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
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
private fun CountdownRow(
    item: CountdownItem,
    onClick: () -> Unit,
    onTogglePinned: () -> Unit,
    onDelete: () -> Unit
) {
    val event = item.event
    var showMenu by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().smoothCardClick(onClick).padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.isPinned) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "已置顶",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = fullDateLabel(event.targetDate),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Text(
                text = compactCountdownText(item.daysRemaining),
                fontWeight = FontWeight.SemiBold,
                color = if (item.daysRemaining >= 0) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(start = 12.dp)
            )
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "${event.title}更多操作")
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text(if (event.isPinned) "取消置顶" else "置顶") },
                        onClick = {
                            showMenu = false
                            onTogglePinned()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
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
    val today = studyDate()
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
    var showTaskForm by rememberSaveable { mutableStateOf(false) }
    var dialogInitialHabitId by rememberSaveable { mutableStateOf<Long?>(null) }
    val habits = tasks.filter { it.isHabit }.sortedByDescending { it.isCore }
    val oneTimeTasks = tasks.filterNot { it.isHabit }.sortedWith(
        compareBy<StudyTaskEntity> { it.completed }
            .thenByDescending { it.isCore }
            .thenByDescending { it.createdAt }
    )

    LaunchedEffect(initialHabitId) {
        if (initialHabitId != null) {
            dialogInitialHabitId = initialHabitId
            showTaskForm = true
            onInitialHabitConsumed()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 88.dp)
        ) {
            item { TaskGroupHeader("长期习惯", habits.size) }
            if (habits.isEmpty()) {
                item { EmptyCard("还没有长期习惯。") }
            } else {
                items(habits, key = { "habit-${it.id}" }) { task ->
                    TaskRow(
                        task = task,
                        onClick = { onEditTask(task) },
                        onToggle = { onToggleTask(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
            item { TaskGroupHeader("今日任务", oneTimeTasks.size) }
            if (oneTimeTasks.isEmpty()) {
                item { EmptyCard("今天还没有一次任务。") }
            } else {
                items(oneTimeTasks, key = { "task-${it.id}" }) { task ->
                    TaskRow(
                        task = task,
                        onClick = { onEditTask(task) },
                        onToggle = { onToggleTask(task) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = {
                dialogInitialHabitId = null
                showTaskForm = true
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "新增任务")
        }
    }

    if (showTaskForm) {
        TaskCreateDialog(
            tasks = tasks,
            initialHabitId = dialogInitialHabitId,
            onDismiss = {
                showTaskForm = false
                dialogInitialHabitId = null
            },
            onAddTask = { title, subject, description, minutes, isHabit, studyType, isCore, habitId ->
                onAddTask(title, subject, description, minutes, isHabit, studyType, isCore, habitId)
                showTaskForm = false
                dialogInitialHabitId = null
            }
        )
    }
}

@Composable
private fun TaskCreateDialog(
    tasks: List<StudyTaskEntity>,
    initialHabitId: Long?,
    onDismiss: () -> Unit,
    onAddTask: (String, String, String, Int, Boolean, String, Boolean, Long?) -> Unit
) {
    var title by rememberSaveable(initialHabitId) { mutableStateOf("") }
    var subject by rememberSaveable(initialHabitId) { mutableStateOf("") }
    var description by rememberSaveable(initialHabitId) { mutableStateOf("") }
    var minutes by rememberSaveable(initialHabitId) { mutableStateOf("45") }
    var taskKind by rememberSaveable(initialHabitId) { mutableStateOf(TaskKind.OneTime) }
    var studyType by rememberSaveable(initialHabitId) { mutableStateOf(StudyTaskType.General) }
    var isCore by rememberSaveable(initialHabitId) { mutableStateOf(false) }
    var selectedHabitId by rememberSaveable(initialHabitId) { mutableStateOf(initialHabitId) }
    var showAdvanced by rememberSaveable(initialHabitId) { mutableStateOf(initialHabitId != null) }
    val habits = tasks.filter { it.isHabit && !it.isArchived }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (taskKind == TaskKind.Habit) "新增习惯" else "新增任务") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    TaskKind.entries.forEachIndexed { index, kind ->
                        SegmentedButton(
                            selected = taskKind == kind,
                            onClick = {
                                taskKind = kind
                                if (kind == TaskKind.Habit) selectedHabitId = null
                            },
                            shape = SegmentedButtonDefaults.itemShape(index, TaskKind.entries.size)
                        ) { Text(kind.label) }
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(60) },
                    label = { Text(if (taskKind == TaskKind.Habit) "习惯名称" else "任务名称") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
                        label = { Text(if (taskKind == TaskKind.Habit) "每日目标" else "预计分钟") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(240) },
                    label = { Text("说明（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
                TextButton(onClick = { showAdvanced = !showAdvanced }) {
                    Text(if (showAdvanced) "收起更多选项" else "更多选项")
                }
                AnimatedVisibility(visible = showAdvanced) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("学习类型", fontWeight = FontWeight.Medium)
                        StudyTypePicker(selected = studyType, onSelected = { studyType = it })
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isCore, onCheckedChange = { isCore = it })
                            Text("设为核心任务")
                        }
                        if (taskKind == TaskKind.OneTime && habits.isNotEmpty()) {
                            Text("所属习惯（可选）", fontWeight = FontWeight.Medium)
                            HabitPicker(
                                habits = habits,
                                selectedHabitId = selectedHabitId,
                                onSelected = { selectedHabitId = it }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
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
                }
            ) { Text("添加") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@Composable
private fun TaskGroupHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Text("$count", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
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
    onPhaseElapsed: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
    onLeaveImmersive: () -> Unit
) {
    val context = LocalContext.current
    val isTacticalStyle = LocalAppVisualStyle.current == AppVisualStyle.Tactical
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
            isTacticalStyle -> showPreRound = true
            else -> requestStart(selectedTaskTitle)
        }
    }

    LaunchedEffect(timerState.isRunning, timerState.endsAt) {
        now = System.currentTimeMillis()
        while (timerState.isRunning) {
            now = System.currentTimeMillis()
            if (timerState.hasPhaseElapsed(now)) {
                onPhaseElapsed()
                break
            }
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
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedTaskTitle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(18.dp))
                        TimerDial(
                            remainingSeconds = remainingSeconds,
                            totalSeconds = timerState.phaseTotalSeconds,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(Modifier.height(18.dp))
                        Text(
                            text = "${timerState.focusMinutes} 分钟专注 · ${timerState.breakMinutes} 分钟休息 · ${timerState.totalCycles} 轮",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
                if (musicState.isAvailable || musicState.title.isNotBlank()) {
                    item { MusicSection(state = musicState, controller = musicController) }
                }
                item { Spacer(Modifier.height(76.dp)) }
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
                    Text(if (isTacticalStyle) "Start Round" else "开始专注")
                }
                FloatingActionButton(
                    onClick = { showFocusSetup = true },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
    val isTacticalStyle = LocalAppVisualStyle.current == AppVisualStyle.Tactical
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
                    text = if (isTacticalStyle) "ROUND ${timerState.currentCycle}/${timerState.totalCycles} · ${timerState.phase.label}" else "第 ${timerState.currentCycle}/${timerState.totalCycles} 轮 · ${timerState.phase.label}",
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
                        Text(if (timerState.isRunning) "暂停" else if (isTacticalStyle) "继续回合" else "继续")
                    }
                    OutlinedButton(onClick = onEnd) {
                        Text(if (isTacticalStyle) "End Round" else "结束专注")
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
    val weeklyMinutes = state.recentStats.sumOf { it.focusMinutes }
    val formLabel = state.formRating.rating?.let { "评分 ${String.format(Locale.getDefault(), "%.2f", it)}" }
        ?: "数据校准中"

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    "近 7 天专注",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    formatTotalMinutes(weeklyMinutes.toLong()),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "今日 ${state.todayFocusMinutes} 分钟 · 连续 ${state.streakDays} 天",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
        item { WeeklyBarChart(state.recentStats) }
        item {
            Column {
                StatsActionRow(
                    title = "专注历史",
                    detail = "查看和补记每次专注",
                    onClick = onOpenFocusHistory
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                StatsActionRow(
                    title = "FORM 学习状态",
                    detail = formLabel,
                    onClick = onOpenFormDetails
                )
            }
        }
        if (state.habitStats.isNotEmpty()) {
            item { TaskGroupHeader("习惯累计", state.habitStats.size) }
            items(state.habitStats, key = { "habit-stat-${it.task.id}" }) { stat ->
                HabitStatRow(stat)
            }
        }
    }
}

@Composable
private fun StatsActionRow(title: String, detail: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().smoothCardClick(onClick).padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun WeeklyBarChart(stats: List<DayStat>) {
    val maxMinutes = stats.maxOfOrNull { it.focusMinutes }?.coerceAtLeast(30) ?: 30
    val average = if (stats.isEmpty()) 0 else stats.sumOf { it.focusMinutes } / stats.size
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("专注趋势", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text(
                "日均 $average 分钟",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(14.dp))
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.height(104.dp).fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            Modifier
                                .width(18.dp)
                                .height(if (stat.focusMinutes == 0) 4.dp else (14 + 86 * ratio).dp)
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stat.task.title,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "今日目标 ${stat.task.estimatedMinutes} 分钟",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Text(
                text = formatTotalMinutes(stat.totalMinutes),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 12.dp),
                maxLines = 1
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
    val view = LocalView.current
    var showMenu by remember { mutableStateOf(false) }
    val typeLabel = StudyTaskType.fromStorage(task.studyType).label
    val durationLabel = if (task.isHabit) {
        "每日 ${task.estimatedMinutes} 分钟"
    } else {
        "${task.estimatedMinutes} 分钟"
    }
    val meta = listOf(typeLabel, task.subject.takeIf { it.isNotBlank() }, durationLabel)
        .filterNotNull()
        .joinToString(" · ")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().smoothCardClick(onClick).padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (task.isHabit) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.Repeat,
                        contentDescription = "长期习惯",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(21.dp)
                    )
                }
            } else {
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
                    }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        task.title,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (task.isCore) {
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "核心任务",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = meta,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (onDelete != null) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "${task.title}更多操作")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("删除", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            }
                        )
                    }
                }
            } else {
                Spacer(Modifier.width(8.dp))
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 48.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
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
private fun EmptyCard(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
}
