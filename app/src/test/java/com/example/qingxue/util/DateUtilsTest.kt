package com.example.qingxue.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class DateUtilsTest {
    private val shanghai = ZoneId.of("Asia/Shanghai")

    @Test
    fun beforeFourBelongsToPreviousStudyDay() {
        val now = ZonedDateTime.of(2026, 7, 21, 3, 59, 59, 0, shanghai)

        assertEquals(LocalDate.of(2026, 7, 20), studyDate(now))
        assertEquals("2026-07-20", todayString(now))
    }

    @Test
    fun fourOClockStartsTheNewStudyDay() {
        val now = ZonedDateTime.of(2026, 7, 21, 4, 0, 0, 0, shanghai)

        assertEquals(LocalDate.of(2026, 7, 21), studyDate(now))
        assertEquals("2026-07-21", todayString(now))
    }

    @Test
    fun nextBoundaryIsAlwaysTheNextFourOClock() {
        val beforeFour = ZonedDateTime.of(2026, 7, 21, 2, 30, 0, 0, shanghai)
        val afterFour = ZonedDateTime.of(2026, 7, 21, 18, 30, 0, 0, shanghai)

        assertEquals(
            ZonedDateTime.of(2026, 7, 21, 4, 0, 0, 0, shanghai),
            nextStudyDayBoundary(beforeFour)
        )
        assertEquals(
            ZonedDateTime.of(2026, 7, 22, 4, 0, 0, 0, shanghai),
            nextStudyDayBoundary(afterFour)
        )
    }

    @Test
    fun timestampUsesTheRequestedLocalTimeZone() {
        val instant = ZonedDateTime.of(2026, 7, 21, 3, 0, 0, 0, shanghai).toInstant()

        assertEquals("2026-07-20", studyDateStringAt(instant.toEpochMilli(), shanghai))
    }
    @Test
    fun recentDateStringsUsesProvidedDayAsWindowEnd() {
        val dates = recentDateStrings(days = 3, today = LocalDate.of(2026, 7, 15))

        assertEquals(
            listOf("2026-07-13", "2026-07-14", "2026-07-15"),
            dates
        )
    }
}