package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import java.time.LocalDate
import java.util.UUID

enum class MedsTestsKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    CLIENT(1),
    TRIAL(2),
    REWARD(3),
    DIFFICULT(4),
    DANGER(5),
    ADDITIONAL_REWARD(6),
    EXPIRATION(7),
    REQUIREMENTS(8),
}

data class MedsTests (
    val id: String,
    val client: String,
    val trial: String,
    val reward: Int,
    val difficult: Float,
    val danger: Int,
    val additionalReward: Int,
    val expiration: LocalDate,
    val requirements: String,
)