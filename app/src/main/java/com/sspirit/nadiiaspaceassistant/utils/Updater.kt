package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

typealias Updater = MutableIntState

@Composable
fun updaterState(): Updater {
    return rememberSaveable { mutableIntStateOf(0) }
}

fun MutableIntState.update() {
    intValue += 1
}

fun Collection<Updater>.update() {
    forEach { it.update() }
}