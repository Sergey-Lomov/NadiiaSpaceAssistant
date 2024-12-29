package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun safeParseLocalDate(string: String, formatter: DateTimeFormatter): LocalDate? {
    return try {
        LocalDate.parse(string, formatter)
    } catch(e: Exception) {
        null
    }
}