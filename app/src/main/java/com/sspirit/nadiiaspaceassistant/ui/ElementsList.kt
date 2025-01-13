package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> ElementsList(array: Array<T>, space: Int = 8, builder: @Composable (T) -> Unit) {
    ElementsList(array.asIterable(), space, builder)
}

@Composable
fun <T> ElementsList(elements: Iterable<T>, space: Int = 8, builder: @Composable (T) -> Unit) {
    FailableElementsList(elements, space) { element ->
        builder(element)
        true
    }
}

@Composable
fun <T> FailableElementsList(elements: Iterable<T>, space: Int = 8, builder: @Composable (T) -> Boolean) {
    for (element in elements) {
        key (element) {
            val success = builder(element)
            if (element !== elements.last() && success) {
                Spacer(Modifier.height(space.dp))
            }
        }
    }
}