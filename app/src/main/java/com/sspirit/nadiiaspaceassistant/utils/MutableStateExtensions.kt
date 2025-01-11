package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.MutableIntState

fun MutableIntState.update() {
    intValue += 1
}