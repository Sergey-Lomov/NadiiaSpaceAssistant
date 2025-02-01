package com.sspirit.nadiiaspaceassistant.services.external_monitor

import java.util.UUID
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

class LocalServerUpdatesNotifier <T> (initial: T) {
    var value: T = initial
        set(value) {
            field = value
            signalAll()
        }
    var waitingTimeout: Int = Int.MAX_VALUE

    private val lock = ReentrantLock()
    private val condition: Condition = lock.newCondition()
    private var valueId = UUID.randomUUID()

    fun waitUpdates() {
        val idOnCall = valueId
        val timeOnCall = System.currentTimeMillis()
        lock.lock()
        try {
            while (idOnCall == valueId && (System.currentTimeMillis() - timeOnCall) < waitingTimeout) {
                condition.await()
            }
        } finally {
            lock.unlock()
        }
    }

    private fun signalAll() {
        lock.lock()
        try {
            valueId = UUID.randomUUID()
            condition.signalAll()
        } finally {
            lock.unlock()
        }
    }
}