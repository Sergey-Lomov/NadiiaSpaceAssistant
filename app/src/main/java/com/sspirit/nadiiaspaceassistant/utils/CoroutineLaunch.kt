package com.sspirit.nadiiaspaceassistant.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun CoroutineLaunch(task: () -> Unit, completion: (() -> Unit)? = null) {
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
        }
    }
}