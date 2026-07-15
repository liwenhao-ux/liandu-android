package com.example.qingxue.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val storageFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val labelFormatter = DateTimeFormatter.ofPattern("M/d")
private val fullLabelFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")

fun todayString(): String = LocalDate.now().format(storageFormatter)

fun recentDateStrings(days: Long = 7, today: LocalDate = LocalDate.now()): List<String> {
    return (days - 1 downTo 0).map { today.minusDays(it).format(storageFormatter) }
}

fun shortDateLabel(date: String): String {
    return LocalDate.parse(date, storageFormatter).format(labelFormatter)
}

fun fullDateLabel(date: String): String {
    return LocalDate.parse(date, storageFormatter).format(fullLabelFormatter)
}

fun daysUntil(date: String, from: LocalDate = LocalDate.now()): Long {
    return ChronoUnit.DAYS.between(from, LocalDate.parse(date, storageFormatter))
}
