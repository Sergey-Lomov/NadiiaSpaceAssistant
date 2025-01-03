package com.sspirit.nadiiaspaceassistant.utils

fun MutableList<String>.write(string: String) {
    add(string)
}

fun MutableList<String>.write(value: Boolean) {
    add(value.toString().uppercase())
}

fun MutableList<String>.write(value: Number) {
    add(value.toString())
}

fun MutableList<String>.write(values: Array<String>, separator: String = ", ") {
    add(values.joinToString(separator))
}