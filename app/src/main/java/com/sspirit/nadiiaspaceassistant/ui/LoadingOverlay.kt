package com.sspirit.nadiiaspaceassistant.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
fun LoadingOverlay(state: MutableState<Boolean>, content: @Composable () -> Unit) {
    if (state.value)
        LoadingIndicator()
    else
        content()
}