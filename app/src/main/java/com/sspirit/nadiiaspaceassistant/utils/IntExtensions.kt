package com.sspirit.nadiiaspaceassistant.utils

fun Int.locationLevelToFloor() : Float = this + 0.5f
fun Int.locationLevelToCeiling() : Float = this - 0.5f