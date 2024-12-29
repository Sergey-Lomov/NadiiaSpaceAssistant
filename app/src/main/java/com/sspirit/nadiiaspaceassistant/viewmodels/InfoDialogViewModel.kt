package com.sspirit.nadiiaspaceassistant.viewmodels

import androidx.compose.runtime.MutableState

data class InfoDialogViewModel(
    val title: String,
    val info: String,
    var actions: MutableMap<String, (MutableState<Boolean>) -> Unit> = mutableMapOf()
)