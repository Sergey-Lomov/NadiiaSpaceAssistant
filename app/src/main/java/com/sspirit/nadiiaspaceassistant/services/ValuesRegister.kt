package com.sspirit.nadiiaspaceassistant.services

import java.util.UUID

private const val maxSize = 150

object ValuesRegister {
    val stack: MutableList<Pair<String, Any>> = mutableListOf()

    fun register(value: Any) : String {
        if (stack.size >= maxSize) stack.removeAt(0)
        val id = UUID.randomUUID().toString()
        stack.add(id to value)
        return id
    }

    inline fun <reified T> get(id: String): T? {
        val value = stack
            .firstOrNull { it.first == id }
            ?.second
            ?: return null
        return if (value is T) value else null
    }
}