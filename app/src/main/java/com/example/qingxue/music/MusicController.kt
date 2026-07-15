package com.example.qingxue.music

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.SystemClock
import android.provider.Settings
import android.service.notification.NotificationListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class MusicState(
    val isPlaying: Boolean = false,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val albumArt: Bitmap? = null,
    val position: Long = 0L,
    val duration: Long = 0L,
    val isAvailable: Boolean = false,
    val needsNotificationAccess: Boolean = false,
    val isConnecting: Boolean = false,
    val connectionIssue: Boolean = false,
    val canSeek: Boolean = false,
    val canSkipNext: Boolean = true,
    val canSkipPrevious: Boolean = true,
    val canPlayPause: Boolean = true
)

private object NotificationListenerConnection {
    val isConnected = MutableStateFlow(false)

    fun update(connected: Boolean) {
        isConnected.value = connected
    }
}

class MusicController(context: Context) {
    private val appContext = context.applicationContext
    private val sessionManager =
        appContext.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private val listenerComponent = ComponentName(appContext, NotificationListener::class.java)
    private val _state = MutableStateFlow(MusicState())
    val state: StateFlow<MusicState> = _state.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var controller: MediaController? = null
    private var sessionListenerRegistered = false
    private var progressJob: Job? = null
    private var reconnectJob: Job? = null
    private var reconnectAttempts = 0

    private val callback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            scope.launch { updateState() }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            scope.launch { updateState() }
        }

        override fun onSessionDestroyed() {
            scope.launch { refresh() }
        }
    }

    private val activeSessionsListener =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            scope.launch { setController(selectController(controllers.orEmpty())) }
        }

    init {
        scope.launch {
            NotificationListenerConnection.isConnected.drop(1).collect {
                refresh()
            }
        }
        refresh()
    }

    /** Re-check access and active media sessions after returning from system settings. */
    fun refresh() {
        reconnectAttempts = 0
        reconnectJob?.cancel()
        reconnectJob = null
        if (!hasNotificationAccess()) {
            disconnectSessions()
            _state.value = MusicState(needsNotificationAccess = true)
            return
        }
        connectToSessions()
    }

    private fun connectToSessions() {
        try {
            val sessions = sessionManager.getActiveSessions(listenerComponent)
            reconnectAttempts = 0
            reconnectJob?.cancel()
            reconnectJob = null
            setController(selectController(sessions))
            if (!sessionListenerRegistered) {
                sessionManager.addOnActiveSessionsChangedListener(
                    activeSessionsListener,
                    listenerComponent
                )
                sessionListenerRegistered = true
            }
        } catch (_: SecurityException) {
            removeSessionListener()
            clearController()
            scheduleReconnect()
        }
    }

    private fun scheduleReconnect() {
        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            _state.value = MusicState(connectionIssue = true)
            return
        }
        reconnectAttempts += 1
        _state.value = MusicState(isConnecting = true)
        NotificationListenerService.requestRebind(listenerComponent)
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            delay(RECONNECT_DELAY_MS)
            reconnectJob = null
            connectToSessions()
        }
    }

    private fun selectController(controllers: List<MediaController>): MediaController? {
        val usable = controllers.filter(::isUsableSession)
        val comparator = compareBy<MediaController> { sessionRank(it) }
            .thenBy { it.playbackState?.lastPositionUpdateTime ?: 0L }
        usable
            .filter { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            .maxWithOrNull(comparator)
            ?.let { return it }

        val currentToken = controller?.sessionToken
        usable.firstOrNull { it.sessionToken == currentToken }?.let { return it }
        return usable.maxWithOrNull(comparator)
    }

    private fun isUsableSession(candidate: MediaController): Boolean {
        val state = candidate.playbackState?.state
        return metadataTitle(candidate.metadata).isNotBlank() || state in setOf(
            PlaybackState.STATE_PLAYING,
            PlaybackState.STATE_PAUSED,
            PlaybackState.STATE_BUFFERING,
            PlaybackState.STATE_CONNECTING
        )
    }


    private fun sessionRank(candidate: MediaController): Int {
        val stateRank = when (candidate.playbackState?.state) {
            PlaybackState.STATE_PLAYING -> 5
            PlaybackState.STATE_BUFFERING,
            PlaybackState.STATE_CONNECTING -> 4
            PlaybackState.STATE_PAUSED -> 3
            PlaybackState.STATE_STOPPED -> 2
            else -> 1
        }
        return stateRank * 10 + if (metadataTitle(candidate.metadata).isNotBlank()) 1 else 0
    }

    private fun setController(next: MediaController?) {
        if (
            controller != null &&
            next != null &&
            controller?.sessionToken == next.sessionToken
        ) {
            updateState()
            return
        }

        clearController()
        controller = next
        controller?.registerCallback(callback)
        if (controller == null) {
            _state.value = MusicState()
        } else {
            updateState()
        }
    }

    private fun clearController() {
        stopProgressUpdates()
        controller?.unregisterCallback(callback)
        controller = null
    }

    private fun removeSessionListener() {
        if (sessionListenerRegistered) {
            sessionManager.removeOnActiveSessionsChangedListener(activeSessionsListener)
            sessionListenerRegistered = false
        }
    }

    private fun disconnectSessions() {
        removeSessionListener()
        clearController()
    }

    private fun hasNotificationAccess(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            appContext.contentResolver,
            "enabled_notification_listeners"
        ).orEmpty()
        return enabledListeners
            .split(':')
            .mapNotNull(ComponentName::unflattenFromString)
            .any { it == listenerComponent }
    }

    fun release() {
        reconnectJob?.cancel()
        reconnectJob = null
        disconnectSessions()
        _state.value = MusicState()
        scope.cancel()
    }

    fun playPause() {
        val ctrl = controller ?: return
        if (_state.value.isPlaying) {
            ctrl.transportControls.pause()
        } else {
            ctrl.transportControls.play()
        }
    }

    fun skipToNext() {
        controller?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        controller?.transportControls?.skipToPrevious()
    }

    fun seekTo(positionMs: Long) {
        val current = _state.value
        if (!current.canSeek || current.duration <= 0L) return
        controller?.transportControls?.seekTo(positionMs.coerceIn(0L, current.duration))
    }

    private fun updateState() {
        val ctrl = controller ?: return
        val metadata = ctrl.metadata
        val playbackState = ctrl.playbackState
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
            ?.coerceAtLeast(0L) ?: 0L
        val isPlaying = playbackState?.state == PlaybackState.STATE_PLAYING
        val actions = playbackState?.actions ?: 0L
        _state.value = MusicState(
            isPlaying = isPlaying,
            title = metadataTitle(metadata),
            artist = metadataArtist(metadata),
            album = metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM).orEmpty(),
            albumArt = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_DISPLAY_ICON),
            position = currentPlaybackPosition(playbackState, duration),
            duration = duration,
            isAvailable = true,
            canSeek = duration > 0L && supportsAction(actions, PlaybackState.ACTION_SEEK_TO),
            canSkipNext = supportsAction(actions, PlaybackState.ACTION_SKIP_TO_NEXT),
            canSkipPrevious = supportsAction(actions, PlaybackState.ACTION_SKIP_TO_PREVIOUS),
            canPlayPause = supportsAction(actions, PlaybackState.ACTION_PLAY_PAUSE) ||
                supportsAction(actions, PlaybackState.ACTION_PLAY) ||
                supportsAction(actions, PlaybackState.ACTION_PAUSE)
        )
        if (isPlaying) startProgressUpdates() else stopProgressUpdates()
    }

    private fun metadataTitle(metadata: MediaMetadata?): String {
        return metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
            ?.takeIf { it.isNotBlank() }
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_TITLE).orEmpty()
    }

    private fun metadataArtist(metadata: MediaMetadata?): String {
        return metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE)
            ?.takeIf { it.isNotBlank() }
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?.takeIf { it.isNotBlank() }
            ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST).orEmpty()
    }

    private fun supportsAction(actions: Long, action: Long): Boolean {
        return actions == 0L || actions and action != 0L
    }

    private fun startProgressUpdates() {
        if (progressJob?.isActive == true) return
        progressJob = scope.launch {
            while (isActive) {
                delay(1_000L)
                val playbackState = controller?.playbackState ?: break
                val current = _state.value
                if (playbackState.state != PlaybackState.STATE_PLAYING) {
                    _state.value = current.copy(
                        isPlaying = false,
                        position = currentPlaybackPosition(playbackState, current.duration)
                    )
                    break
                }
                _state.value = current.copy(
                    position = currentPlaybackPosition(playbackState, current.duration)
                )
            }
        }
    }

    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun currentPlaybackPosition(state: PlaybackState?, duration: Long): Long {
        if (state == null) return 0L
        return calculatePlaybackPosition(
            basePosition = state.position,
            lastPositionUpdateTime = state.lastPositionUpdateTime,
            nowElapsedRealtime = SystemClock.elapsedRealtime(),
            playbackSpeed = state.playbackSpeed,
            isPlaying = state.state == PlaybackState.STATE_PLAYING,
            duration = duration
        )
    }

    private companion object {
        const val MAX_RECONNECT_ATTEMPTS = 3
        const val RECONNECT_DELAY_MS = 1_500L
    }
}

internal fun calculatePlaybackPosition(
    basePosition: Long,
    lastPositionUpdateTime: Long,
    nowElapsedRealtime: Long,
    playbackSpeed: Float,
    isPlaying: Boolean,
    duration: Long
): Long {
    val safeBase = basePosition.coerceAtLeast(0L)
    val position = if (isPlaying && lastPositionUpdateTime > 0L) {
        val elapsed = (nowElapsedRealtime - lastPositionUpdateTime).coerceAtLeast(0L)
        safeBase + (elapsed * playbackSpeed).toLong()
    } else {
        safeBase
    }
    return if (duration > 0L) position.coerceIn(0L, duration) else position.coerceAtLeast(0L)
}

/**
 * NotificationListener gives MediaSessionManager a component whose notification access
 * can be granted by the user. Without that access, active media sessions are unavailable.
 */
class NotificationListener : NotificationListenerService() {
    override fun onListenerConnected() {
        super.onListenerConnected()
        NotificationListenerConnection.update(true)
    }

    override fun onListenerDisconnected() {
        NotificationListenerConnection.update(false)
        requestRebind(ComponentName(this, NotificationListener::class.java))
        super.onListenerDisconnected()
    }

    override fun onDestroy() {
        NotificationListenerConnection.update(false)
        super.onDestroy()
    }

    override fun onNotificationPosted(srn: android.service.notification.StatusBarNotification?) {}
    override fun onNotificationRemoved(srn: android.service.notification.StatusBarNotification?) {}
}