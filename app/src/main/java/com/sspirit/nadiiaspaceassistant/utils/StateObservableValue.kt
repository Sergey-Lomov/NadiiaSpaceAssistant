package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.MutableState

class StateObservableValue<T> (value: T) {
    var value: T = value
        set(value) {
            field = value
            observers.forEach { it.value = value }
        }

    private val observers: MutableList<MutableState<T>> = mutableListOf()

    fun addObserver(observer: MutableState<T>) {
        observers.add(observer)
    }

    fun removeObserver(observer: MutableState<T>) {
        observers.remove(observer)
    }
}