package com.sspirit.nadiiaspaceassistant.extensions

import java.util.Locale

fun Float.toString(signs: Int): String {
    return String.format(Locale.US, "%.${signs}f", this)
}