package com.example.qingxue.util

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {
    @Test
    fun recentDateStringsUsesProvidedDayAsWindowEnd() {
        val dates = recentDateStrings(days = 3, today = LocalDate.of(2026, 7, 15))

        assertEquals(
            listOf("2026-07-13", "2026-07-14", "2026-07-15"),
            dates
        )
    }
}