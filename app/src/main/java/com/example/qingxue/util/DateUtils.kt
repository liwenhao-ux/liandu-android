package com.example.qingxue.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val storageFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val labelFormatter = DateTimeFormatter.ofPattern("M/d")
const val STUDY_DAY_START_HOUR = 4
private val studyDayStart = LocalTime.of(STUDY_DAY_START_HOUR, 0)
private val fullLabelFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")

fun studyDate(now: ZonedDateTime = ZonedDateTime.now()): LocalDate =
    if (now.toLocalTime().isBefore(studyDayStart)) now.toLocalDate().minusDays(1)
    else now.toLocalDate()

fun todayString(now: ZonedDateTime = ZonedDateTime.now()): String =
    studyDate(now).format(storageFormatter)

fun studyDateStringAt(
    timestampMillis: Long,
    zoneId: ZoneId = ZoneId.systemDefault()
): String = studyDate(Instant.ofEpochMilli(timestampMillis).atZone(zoneId)).format(storageFormatter)

fun nextStudyDayBoundary(now: ZonedDateTime = ZonedDateTime.now()): ZonedDateTime =
    studyDate(now).plusDays(1).atTime(studyDayStart).atZone(now.zone)

fun recentDateStrings(days: Long = 7, today: LocalDate = studyDate()): List<String> {
    return (days - 1 downTo 0).map { today.minusDays(it).format(storageFormatter) }
}

fun shortDateLabel(date: String): String {
    return LocalDate.parse(date, storageFormatter).format(labelFormatter)
}

fun fullDateLabel(date: String): String {
    return LocalDate.parse(date, storageFormatter).format(fullLabelFormatter)
}

fun daysUntil(date: String, from: LocalDate = studyDate()): Long {
    return ChronoUnit.DAYS.between(from, LocalDate.parse(date, storageFormatter))
}
