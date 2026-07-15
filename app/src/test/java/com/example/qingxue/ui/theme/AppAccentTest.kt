package com.example.qingxue.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AppAccentTest {
    @Test
    fun storageKeysRoundTrip() {
        AppAccent.entries.forEach { accent ->
            assertEquals(accent, AppAccent.fromStorage(accent.storageKey))
        }
    }

    @Test
    fun unknownValueFallsBackToGrayPurple() {
        assertEquals(AppAccent.GrayPurple, AppAccent.fromStorage("unknown"))
        assertEquals(AppAccent.GrayPurple, AppAccent.fromStorage(null))
    }

    @Test
    fun storageKeysAreUnique() {
        assertTrue(AppAccent.entries.map { it.storageKey }.toSet().size == AppAccent.entries.size)
    }
}