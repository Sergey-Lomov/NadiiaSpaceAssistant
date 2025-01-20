package com.sspirit.nadiiaspaceassistant.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

fun MutableList<String>.write(string: String) {
    add(string)
}

fun MutableList<String>.write(value: Boolean) {
    add(value.toString().uppercase())
}

fun MutableList<String>.write(value: Number) {
    add(value.toString())
}

fun MutableList<String>.write(value: LocalDate, formatter: DateTimeFormatter = localDateFormatter) {
    add(value.format(formatter))
}

fun MutableList<String>.writeOptional(value: LocalDate?, formatter: DateTimeFormatter = localDateFormatter) {
    if (value == null)
        add("")
    else
        add(value.format(formatter))
}

fun MutableList<String>.write(values: Array<String>, separator: String = ", ") {
    add(values.joinToString(separator))
}