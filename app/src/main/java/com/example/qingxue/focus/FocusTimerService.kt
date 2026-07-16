package com.example.qingxue.focus

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import com.example.qingxue.MainActivity
import com.example.qingxue.QingXueApp
import com.example.qingxue.R
import com.example.qingxue.data.FocusEndReason
import com.example.qingxue.data.StudyRepository
import com.example.qingxue.util.todayString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusTimerService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val app: QingXueApp get() = application as QingXueApp
    private val store: FocusTimerStore get() = app.focusTimerStore
    private val repository: StudyRepository get() = app.repository
    private val haptics by lazy { FocusHaptics(this) }
    private var phaseCompletionJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        serviceRunning = true
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopTimerService()
            ACTION_PAUSE -> serviceScope.launch { pauseTimer() }
            ACTION_END -> serviceScope.launch { endTimer() }
            ACTION_START_OR_RESUME -> {
                startForegroundCompat(buildPreviewNotification(intent))
                serviceScope.launch { startOrResumeTimer(intent) }
            }
            ACTION_RESTORE, null -> {
                startForegroundCompat(buildRestoringNotification())
                serviceScope.launch { restoreTimer() }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        serviceRunning = false
        phaseCompletionJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    private suspend fun startOrResumeTimer(intent: Intent) {
        val current = store.currentTimerState()
        if (current.isRunning) {
            publish(current)
            return
        }

        val now = System.currentTimeMillis()
        val startingFresh = !current.hasStarted
        val base = if (startingFresh) {
            val focusMinutes = intent.getIntExtra(EXTRA_FOCUS_MINUTES, current.focusMinutes)
                .coerceIn(1, 120)
            val breakMinutes = intent.getIntExtra(EXTRA_BREAK_MINUTES, current.breakMinutes)
                .coerceIn(1, 60)
            val cycles = intent.getIntExtra(EXTRA_TOTAL_CYCLES, current.totalCycles)
                .coerceIn(1, 8)
            FocusTimerState(
                focusMinutes = focusMinutes,
                breakMinutes = breakMinutes,
                totalCycles = cycles,
                pausedRemainingSeconds = focusMinutes * 60,
                activeTaskId = intent.longExtraOrNull(EXTRA_TASK_ID),
                activeHabitId = intent.longExtraOrNull(EXTRA_HABIT_ID),
                activeTaskTitle = intent.getStringExtra(EXTRA_TASK_TITLE)
            )
        } else {
            current
        }
        val remaining = base.pausedRemainingSeconds.coerceIn(1, base.phaseTotalSeconds)
        val completedPauseSeconds = if (
            base.phase == PomodoroPhase.Focus && base.pausedAt > 0L
        ) {
            ((now - base.pausedAt).coerceAtLeast(0L) / 1_000L).toInt()
        } else {
            0
        }
        val updated = base.copy(
            isRunning = true,
            pausedRemainingSeconds = remaining,
            startedAt = base.startedAt.takeIf { it > 0L } ?: now,
            endsAt = now + remaining * 1_000L,
            pausedSeconds = base.pausedSeconds + completedPauseSeconds,
            pausedAt = 0L
        )
        store.saveTimerState(updated)
        publish(updated)
        haptics.play(if (startingFresh) FocusHapticCue.Start else FocusHapticCue.Resume)
    }

    private suspend fun pauseTimer() {
        val current = store.currentTimerState()
        if (!current.isRunning) {
            publish(current)
            return
        }
        val remaining = current.remainingSeconds()
        if (remaining == 0) {
            advancePhase(current.endsAt)
            return
        }

        phaseCompletionJob?.cancel()
        val updated = current.copy(
            isRunning = false,
            pausedRemainingSeconds = remaining,
            endsAt = 0L,
            pauseCount = current.pauseCount +
                if (current.phase == PomodoroPhase.Focus) 1 else 0,
            pausedAt = System.currentTimeMillis()
        )
        store.saveTimerState(updated)
        publish(updated)
        haptics.play(FocusHapticCue.Pause)
    }

    private suspend fun endTimer() {
        val current = store.currentTimerState()
        if (!current.hasStarted) {
            stopTimerService()
            return
        }
        val endedAt = System.currentTimeMillis()
        val remaining = current.remainingSeconds(endedAt)
        val finishedCurrentFocus = current.phase == PomodoroPhase.Focus && remaining == 0
        val completedCycles = current.completedCycles + if (finishedCurrentFocus) 1 else 0
        val finalState = current.copy(completedCycles = completedCycles)
        val completedPlan = completedCycles >= current.totalCycles

        phaseCompletionJob?.cancel()
        saveSession(
            state = finalState,
            endedAt = endedAt,
            actualSeconds = current.actualFocusSeconds(endedAt),
            pausedSeconds = totalPausedSeconds(current, endedAt),
            endReason = if (completedPlan) {
                FocusEndReason.Completed
            } else {
                FocusEndReason.EndedEarly
            }
        )
        store.saveTimerState(current.resetForNextPlan())
        haptics.play(FocusHapticCue.End)
        stopTimerService()
    }

    private suspend fun restoreTimer() {
        val state = store.currentTimerState()
        if (!state.hasStarted) {
            stopTimerService()
            return
        }
        if (state.isRunning && state.endsAt <= System.currentTimeMillis()) {
            advancePhase(state.endsAt)
        } else {
            publish(state)
        }
    }

    private fun schedulePhaseCompletion(state: FocusTimerState) {
        phaseCompletionJob?.cancel()
        if (!state.isRunning) return
        phaseCompletionJob = serviceScope.launch {
            delay((state.endsAt - System.currentTimeMillis()).coerceAtLeast(0L))
            advancePhase(state.endsAt)
        }
    }

    private suspend fun advancePhase(expectedEndsAt: Long) {
        val current = store.currentTimerState()
        if (!current.isRunning || current.endsAt != expectedEndsAt) return

        val now = System.currentTimeMillis()
        if (current.phase == PomodoroPhase.Focus) {
            val completedCycles = current.completedCycles + 1
            val completedFocusSeconds = current.completedFocusSeconds + current.phaseTotalSeconds
            val finishedState = current.copy(
                completedCycles = completedCycles,
                completedFocusSeconds = completedFocusSeconds,
                pausedRemainingSeconds = 0
            )
            if (completedCycles >= current.totalCycles) {
                saveSession(
                    state = finishedState,
                    endedAt = now,
                    actualSeconds = completedFocusSeconds,
                    pausedSeconds = current.pausedSeconds,
                    endReason = FocusEndReason.Completed
                )
                store.saveTimerState(current.resetForNextPlan())
                showCompletionNotification(current.activeTaskTitle)
                haptics.play(FocusHapticCue.PlanComplete)
                stopTimerService()
            } else {
                val breakState = finishedState.copy(
                    phase = PomodoroPhase.Break,
                    isRunning = true,
                    pausedRemainingSeconds = current.breakMinutes * 60,
                    endsAt = now + current.breakMinutes * 60_000L,
                    pausedAt = 0L
                )
                store.saveTimerState(breakState)
                haptics.play(FocusHapticCue.FocusComplete)
                publish(breakState)
            }
        } else {
            val focusState = current.copy(
                phase = PomodoroPhase.Focus,
                currentCycle = (current.currentCycle + 1).coerceAtMost(current.totalCycles),
                isRunning = true,
                pausedRemainingSeconds = current.focusMinutes * 60,
                endsAt = now + current.focusMinutes * 60_000L,
                pausedAt = 0L
            )
            store.saveTimerState(focusState)
            haptics.play(FocusHapticCue.BreakComplete)
            publish(focusState)
        }
    }

    private suspend fun saveSession(
        state: FocusTimerState,
        endedAt: Long,
        actualSeconds: Int,
        pausedSeconds: Int,
        endReason: FocusEndReason
    ) {
        if (actualSeconds < 60) return
        val startedAt = state.startedAt.takeIf { it > 0L }
            ?: endedAt - actualSeconds * 1_000L
        val sessionId = repository.recordFocus(
            taskId = state.activeTaskId,
            habitId = state.activeHabitId,
            startedAt = startedAt,
            endedAt = endedAt,
            plannedMinutes = state.focusMinutes * state.totalCycles,
            actualSeconds = actualSeconds,
            pauseCount = state.pauseCount,
            pausedSeconds = pausedSeconds,
            endReason = endReason,
            focusBlockMinutes = state.focusMinutes,
            breakMinutes = state.breakMinutes,
            plannedCycles = state.totalCycles,
            completedCycles = state.completedCycles,
            date = todayString()
        )
        if (sessionId > 0L) {
            store.savePendingSettlement(
                PendingFocusSettlement(
                    sessionId = sessionId,
                    taskTitle = state.activeTaskTitle,
                    actualMinutes = actualSeconds / 60,
                    completedTimer = endReason == FocusEndReason.Completed
                )
            )
        }
    }

    private fun totalPausedSeconds(state: FocusTimerState, nowMillis: Long): Int {
        val openPauseSeconds = if (
            !state.isRunning &&
            state.phase == PomodoroPhase.Focus &&
            state.pausedAt > 0L
        ) {
            ((nowMillis - state.pausedAt).coerceAtLeast(0L) / 1_000L).toInt()
        } else {
            0
        }
        return state.pausedSeconds + openPauseSeconds
    }

    private fun publish(state: FocusTimerState) {
        startForegroundCompat(buildNotification(state))
        schedulePhaseCompletion(state)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "专注倒计时（锁屏）",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "在通知栏和锁屏显示当前番茄钟"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
            enableVibration(false)
            setSound(null, null)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(state: FocusTimerState): Notification {
        val phaseLabel = state.phase.label
        val statusLabel = if (state.isRunning) "${phaseLabel}中" else "${phaseLabel}已暂停"
        val cycleLabel = "第 ${state.currentCycle}/${state.totalCycles} 轮"
        val remainingSeconds = state.remainingSeconds()
        val staticTime = "%02d:%02d".format(remainingSeconds / 60, remainingSeconds % 60)
        val detail = listOfNotNull(cycleLabel, state.activeTaskTitle).joinToString(" · ")
        val toggleAction = if (state.isRunning) ACTION_PAUSE else ACTION_START_OR_RESUME
        val toggleLabel = if (state.isRunning) "暂停" else "继续"

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_focus)
            .setColor(Color.rgb(27, 111, 99))
            .setContentTitle("练度 · $statusLabel")
            .setContentText(if (state.isRunning) detail else "$detail · $staticTime")
            .setSubText(cycleLabel)
            .setContentIntent(openAppPendingIntent())
            .setCategory(Notification.CATEGORY_STOPWATCH)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(state.isRunning)
            .setWhen(if (state.isRunning) state.endsAt else System.currentTimeMillis())
            .setUsesChronometer(state.isRunning)
            .setChronometerCountDown(state.isRunning)
            .addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.ic_stat_focus),
                    toggleLabel,
                    servicePendingIntent(toggleAction, 1)
                ).build()
            )
            .addAction(
                Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.ic_stat_focus),
                    "结束",
                    servicePendingIntent(ACTION_END, 2)
                ).build()
            )
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
                }
            }
            .build()
    }

    private fun buildPreviewNotification(intent: Intent): Notification {
        val focusMinutes = intent.getIntExtra(EXTRA_FOCUS_MINUTES, 25).coerceIn(1, 120)
        return buildNotification(
            FocusTimerState(
                focusMinutes = focusMinutes,
                breakMinutes = intent.getIntExtra(EXTRA_BREAK_MINUTES, 5).coerceIn(1, 60),
                totalCycles = intent.getIntExtra(EXTRA_TOTAL_CYCLES, 2).coerceIn(1, 8),
                isRunning = true,
                startedAt = System.currentTimeMillis(),
                endsAt = System.currentTimeMillis() + focusMinutes * 60_000L,
                pausedRemainingSeconds = focusMinutes * 60,
                activeTaskTitle = intent.getStringExtra(EXTRA_TASK_TITLE)
            )
        )
    }

    private fun buildRestoringNotification(): Notification {
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_focus)
            .setContentTitle("练度 · 正在恢复番茄钟")
            .setContentIntent(openAppPendingIntent())
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun showCompletionNotification(taskTitle: String?) {
        val text = taskTitle?.let { "$it · 本轮计划已完成" } ?: "本轮番茄计划已完成"
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_focus)
            .setColor(Color.rgb(27, 111, 99))
            .setContentTitle("练度 · 专注完成")
            .setContentText(text)
            .setContentIntent(openAppPendingIntent())
            .setCategory(Notification.CATEGORY_REMINDER)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()
        getSystemService(NotificationManager::class.java).notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun servicePendingIntent(action: String, requestCode: Int): PendingIntent {
        return PendingIntent.getService(
            this,
            requestCode,
            Intent(this, FocusTimerService::class.java).setAction(action),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun startForegroundCompat(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun stopTimerService() {
        phaseCompletionJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun Intent.longExtraOrNull(key: String): Long? {
        return if (hasExtra(key)) getLongExtra(key, 0L) else null
    }

    companion object {
        private const val CHANNEL_ID = "focus_timer_lockscreen_v2"
        private const val NOTIFICATION_ID = 1102
        private const val COMPLETION_NOTIFICATION_ID = 1103
        private const val ACTION_START_OR_RESUME = "com.example.qingxue.focus.START_OR_RESUME"
        private const val ACTION_PAUSE = "com.example.qingxue.focus.PAUSE"
        private const val ACTION_END = "com.example.qingxue.focus.END"
        private const val ACTION_RESTORE = "com.example.qingxue.focus.RESTORE"
        private const val ACTION_STOP = "com.example.qingxue.focus.STOP"
        private const val EXTRA_FOCUS_MINUTES = "focus_minutes"
        private const val EXTRA_BREAK_MINUTES = "break_minutes"
        private const val EXTRA_TOTAL_CYCLES = "total_cycles"
        private const val EXTRA_TASK_ID = "task_id"
        private const val EXTRA_HABIT_ID = "habit_id"
        private const val EXTRA_TASK_TITLE = "task_title"

        @Volatile
        private var serviceRunning = false

        fun startOrResume(
            context: Context,
            state: FocusTimerState,
            taskId: Long?,
            habitId: Long?,
            taskTitle: String?
        ) {
            val intent = Intent(context, FocusTimerService::class.java).apply {
                action = ACTION_START_OR_RESUME
                putExtra(EXTRA_FOCUS_MINUTES, state.focusMinutes)
                putExtra(EXTRA_BREAK_MINUTES, state.breakMinutes)
                putExtra(EXTRA_TOTAL_CYCLES, state.totalCycles)
                taskId?.let { putExtra(EXTRA_TASK_ID, it) }
                habitId?.let { putExtra(EXTRA_HABIT_ID, it) }
                putExtra(EXTRA_TASK_TITLE, taskTitle)
            }
            if (serviceRunning) context.startService(intent) else context.startForegroundService(intent)
        }

        fun pause(context: Context) {
            context.startService(
                Intent(context, FocusTimerService::class.java).setAction(ACTION_PAUSE)
            )
        }

        fun end(context: Context) {
            context.startService(
                Intent(context, FocusTimerService::class.java).setAction(ACTION_END)
            )
        }

        fun restore(context: Context) {
            context.startForegroundService(
                Intent(context, FocusTimerService::class.java).setAction(ACTION_RESTORE)
            )
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, FocusTimerService::class.java))
            serviceRunning = false
        }
    }
}
