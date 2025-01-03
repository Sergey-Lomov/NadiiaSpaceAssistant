package com.sspirit.nadiiaspaceassistant.services

import java.util.UUID

private const val maxSize = 50

private data class ClosureRegistration(
    val closure: () -> Unit,
    val id: String = UUID.randomUUID().toString(),
)

