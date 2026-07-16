package com.example.qingxue.focus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusHapticPatternsTest {
    @Test
    fun everyCueHasAValidNonRepeatingWaveform() {
        FocusHapticCue.entries.forEach { cue ->
            val waveform = FocusHapticPatterns.waveform(cue)
            assertEquals(waveform.timings.size, waveform.amplitudes.size)
            assertEquals(0L, waveform.timings.first())
            assertEquals(0, waveform.amplitudes.first())
            assertTrue(waveform.timings.drop(1).all { it > 0L })
            assertTrue(waveform.amplitudes.all { it in 0..255 })
            assertTrue(waveform.amplitudes.drop(1).any { it > 0 })
        }
    }

    @Test
    fun completionCuesAreMoreDistinctThanStart() {
        val start = FocusHapticPatterns.waveform(FocusHapticCue.Start)
        val focusComplete = FocusHapticPatterns.waveform(FocusHapticCue.FocusComplete)
        val planComplete = FocusHapticPatterns.waveform(FocusHapticCue.PlanComplete)

        assertTrue(focusComplete.timings.sum() > start.timings.sum())
        assertTrue(planComplete.timings.sum() > focusComplete.timings.sum())
    }
}