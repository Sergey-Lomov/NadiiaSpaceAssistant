package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun safeParseLocalDate(string: String, formatter: DateTimeFormatter): LocalDate? {
    return try {
        LocalDate.parse(string, formatter)
    } catch(e: Exception) {
        null
    }
}

fun LocalDate.daysToNow(): Int = ChronoUnit.DAYS.between(LocalDate.now(), this).toInt()