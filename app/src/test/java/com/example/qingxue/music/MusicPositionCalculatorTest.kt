package com.example.qingxue.music

import org.junit.Assert.assertEquals
import org.junit.Test

class MusicPositionCalculatorTest {
    @Test
    fun playingPositionAdvancesFromPlaybackClock() {
        assertEquals(
            12_500L,
            calculatePlaybackPosition(
                basePosition = 10_000L,
                lastPositionUpdateTime = 1_000L,
                nowElapsedRealtime = 3_500L,
                playbackSpeed = 1f,
                isPlaying = true,
                duration = 60_000L
            )
        )
    }

    @Test
    fun pausedPositionDoesNotAdvance() {
        assertEquals(
            10_000L,
            calculatePlaybackPosition(
                basePosition = 10_000L,
                lastPositionUpdateTime = 1_000L,
                nowElapsedRealtime = 9_000L,
                playbackSpeed = 1f,
                isPlaying = false,
                duration = 60_000L
            )
        )
    }

    @Test
    fun positionIsClampedToKnownDuration() {
        assertEquals(
            60_000L,
            calculatePlaybackPosition(
                basePosition = 59_000L,
                lastPositionUpdateTime = 1_000L,
                nowElapsedRealtime = 5_000L,
                playbackSpeed = 1f,
                isPlaying = true,
                duration = 60_000L
            )
        )
    }
}