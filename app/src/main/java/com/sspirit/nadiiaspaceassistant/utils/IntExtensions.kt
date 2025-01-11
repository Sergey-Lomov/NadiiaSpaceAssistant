package com.sspirit.nadiiaspaceassistant.utils

import java.util.Locale

fun Int.locationLevelToFloor() : Float = this + 0.5f
fun Int.locationLevelToCeiling() : Float = this - 0.5f
fun Int.toString(signs: Int): String = this.toString().padStart(signs, '0')