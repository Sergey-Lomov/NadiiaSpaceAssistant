package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val localDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun safeParseLocalDate(
    string: String,
    formatter: DateTimeFormatter = localDateFormatter
): LocalDate? {
    return try {
        LocalDate.parse(string, formatter)
    } catch(e: Exception) {
        null
    }
}

fun LocalDate.parse(string: String): LocalDate = LocalDate.parse(string, localDateFormatter)
fun LocalDate.format(): String = format(localDateFormatter)
fun LocalDate.daysToNow(): Int = ChronoUnit.DAYS.between(LocalDate.now(), this).toInt()
fun LocalDate.plusDays(days: Int): LocalDate = plusDays(days.toLong())

fun LocalDateTime.plusHours(hours: Int): LocalDateTime = plusHours(hours.toLong())