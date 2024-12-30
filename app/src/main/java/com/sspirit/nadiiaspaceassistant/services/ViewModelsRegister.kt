package com.sspirit.nadiiaspaceassistant.services

import java.util.UUID

object ViewModelsRegister {
    val map: MutableMap<String, Any> = mutableMapOf()

    fun register(value: Any) : String {
        val id = UUID.randomUUID().toString()
        map[id] = value
        return id
    }

    fun unregister(id: String) {
        map.remove(id)
    }

    inline fun <reified T> get(id: String): T? {
        val value = map[id] ?: return null
        return if (value is T) value else null
    }
}