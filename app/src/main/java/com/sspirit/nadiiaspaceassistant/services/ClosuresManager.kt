package com.sspirit.nadiiaspaceassistant.services

import java.util.UUID

private const val maxSize = 50

private data class ClosureRegistration(
    val closure: () -> Unit,
    val id: String = UUID.randomUUID().toString(),
)

object ClosuresManager {
    private val closures: MutableList<ClosureRegistration> = mutableListOf()

    fun register(closure: () -> Unit) : String {
        val registration = ClosureRegistration(closure)
        if (closures.size >= maxSize) closures.removeAt(0)
        closures.add(registration)
        return registration.id
    }

    fun get(id: String) : (() -> Unit)? =
        closures.firstOrNull { it.id == id }?.closure
}