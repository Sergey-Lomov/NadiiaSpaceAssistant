package com.sspirit.nadiiaspaceassistant.utils

import java.util.Locale

fun Float.toString(signs: Int): String {
    return String.format(Locale.US, "%.${signs}f", this)
}

fun Float.toPercentage(signs: Int = 0): String {
    return String.format(Locale.US, "%.${signs}f", this * 100) + "%"
}