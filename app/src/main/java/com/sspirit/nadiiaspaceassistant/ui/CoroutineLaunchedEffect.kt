package com.sspirit.nadiiaspaceassistant.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CoroutineLaunchedEffect(
    key: Any? = Unit,
    loadingState: MutableState<Boolean>,
    block: () -> Unit
) {
    LaunchedEffect(key) {
        loadingState.value = true
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e("Coroutine launch effect error: ", e.toString())
            }
        }
        job.invokeOnCompletion {
            CoroutineScope(Dispatchers.Main).launch {
                loadingState.value = false
            }
        }
    }
}