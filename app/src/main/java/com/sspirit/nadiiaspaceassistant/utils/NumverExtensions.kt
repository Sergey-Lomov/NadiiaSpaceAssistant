package com.sspirit.nadiiaspaceassistant.utils

fun Number.toSignedString(): String {
    return if (this.toDouble() >= 0) "+$this" else "$this"
}