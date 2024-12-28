package com.sspirit.nadiiaspaceassistant.utils

import android.util.Log
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun mainLaunch(task: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        task()
    }
}

fun simpleCoroutineLaunch(state: MutableState<Boolean>? = null, task: () -> Unit) {
    coroutineLaunch(state, task, null)
}

fun coroutineLaunch(state: MutableState<Boolean>? = null, task: () -> Unit, completion: (() -> Unit)? = null) {
    state?.value = true
    val job = CoroutineScope(Dispatchers.IO).launch {
        try {
            task.invoke()
        } catch (e: Exception) {
            Log.e("Coroutine error", e.toString())
        }
    }
    job.invokeOnCompletion {
        CoroutineScope(Dispatchers.Main).launch {
            completion?.invoke()
            state?.value = false
        }
    }
}