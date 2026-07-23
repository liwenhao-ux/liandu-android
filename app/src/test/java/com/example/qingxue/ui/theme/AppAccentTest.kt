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
    fun unknownValueFallsBackToMistGreen() {
        assertEquals(AppAccent.MistGreen, AppAccent.fromStorage("unknown"))
        assertEquals(AppAccent.MistGreen, AppAccent.fromStorage(null))
    }

    @Test
    fun storageKeysAreUnique() {
        assertTrue(AppAccent.entries.map { it.storageKey }.toSet().size == AppAccent.entries.size)
    }

    @Test
    fun visualStyleStorageRoundTripsAndDefaultsToStandard() {
        AppVisualStyle.entries.forEach { style ->
            assertEquals(style, AppVisualStyle.fromStorage(style.storageKey))
        }
        assertEquals(AppVisualStyle.Standard, AppVisualStyle.fromStorage("unknown"))
        assertEquals(AppVisualStyle.Standard, AppVisualStyle.fromStorage(null))
    }
}