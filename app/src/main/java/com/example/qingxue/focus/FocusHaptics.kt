package com.example.qingxue.focus

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

internal enum class FocusHapticCue {
    Start,
    Resume,
    Pause,
    End,
    FocusComplete,
    BreakComplete,
    PlanComplete
}

internal data class HapticWaveform(
    val timings: LongArray,
    val amplitudes: IntArray
)

internal object FocusHapticPatterns {
    fun waveform(cue: FocusHapticCue): HapticWaveform = when (cue) {
        FocusHapticCue.Start -> HapticWaveform(longArrayOf(0, 35), intArrayOf(0, 90))
        FocusHapticCue.Resume -> HapticWaveform(longArrayOf(0, 28), intArrayOf(0, 70))
        FocusHapticCue.Pause -> HapticWaveform(
            longArrayOf(0, 24, 55, 24),
            intArrayOf(0, 75, 0, 75)
        )
        FocusHapticCue.End -> HapticWaveform(
            longArrayOf(0, 45, 45, 75),
            intArrayOf(0, 105, 0, 145)
        )
        FocusHapticCue.FocusComplete -> HapticWaveform(
            longArrayOf(0, 55, 65, 85),
            intArrayOf(0, 115, 0, 160)
        )
        FocusHapticCue.BreakComplete -> HapticWaveform(
            longArrayOf(0, 28, 45, 28),
            intArrayOf(0, 70, 0, 70)
        )
        FocusHapticCue.PlanComplete -> HapticWaveform(
            longArrayOf(0, 60, 55, 60, 55, 110),
            intArrayOf(0, 115, 0, 145, 0, 190)
        )
    }
}

internal class FocusHaptics(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun play(cue: FocusHapticCue) {
        val deviceVibrator = vibrator?.takeIf(Vibrator::hasVibrator) ?: return
        val waveform = FocusHapticPatterns.waveform(cue)
        runCatching {
            deviceVibrator.vibrate(
                VibrationEffect.createWaveform(waveform.timings, waveform.amplitudes, -1)
            )
        }
    }
}