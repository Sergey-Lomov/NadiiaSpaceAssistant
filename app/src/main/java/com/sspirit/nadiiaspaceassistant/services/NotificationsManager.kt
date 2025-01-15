package com.sspirit.nadiiaspaceassistant.services

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.sspirit.nadiiaspaceassistant.screens.NotificationType
import java.util.Timer
import java.util.TimerTask

private const val DEFAULT_DURATION = 10

private data class Notification (
    val message: String,
    val type: NotificationType
)

object NotificationsManager {
    var isVisibleSate = mutableStateOf(false)
    var messageState= mutableStateOf("")
    var typeState = mutableStateOf(NotificationType.NEUTRAL)
    private var queue: MutableList<Notification> = mutableListOf()
    private var timer: Timer? = null


    private class HideTask : TimerTask() {
        override fun run() = hideCurrentNotification()
    }

    private fun showNextNotification() = synchronized(queue) {
        if (queue.isEmpty()) return@synchronized
        val next = queue.removeAt(0)
        isVisibleSate.value = true
        messageState.value = next.message
        typeState.value = next.type

        timer = Timer()
        val duration = (DEFAULT_DURATION * 1000).toLong()
        timer?.schedule(HideTask(), duration)
    }

    fun addNotification(message: String, type: NotificationType = NotificationType.NEUTRAL) {
        synchronized(queue) {
            queue.add(Notification(message, type))
            if (timer == null)
                showNextNotification()
        }
    }

    fun hideCurrentNotification() {
        isVisibleSate.value = false
        timer?.cancel()
        timer = null
        showNextNotification()
    }
}