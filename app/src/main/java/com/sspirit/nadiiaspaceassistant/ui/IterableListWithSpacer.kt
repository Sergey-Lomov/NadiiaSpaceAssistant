package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> IterableListWithSpacer(array: Array<T>, space: Int = 8, builder: @Composable (T) -> Unit) {
    IterableListWithSpacer(array.asIterable(), space, builder)
}

@Composable
fun <T> IterableListWithSpacer(elements: Iterable<T>, space: Int = 8, builder: @Composable (T) -> Unit) {
    for (element in elements) {
        key (element) {
            builder(element)
        }
        if (element !== elements.last()) {
            Spacer(Modifier.height(space.dp))
        }
    }
}