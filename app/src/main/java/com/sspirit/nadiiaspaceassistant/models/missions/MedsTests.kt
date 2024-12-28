package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.utils.IndexConvertible
import java.time.LocalDate

enum class MedsTestsKeys(override val index: Int) : IndexConvertible {
    ID(0),
    CLIENT(1),
    REWARD(2),
    DIFFICULT(3),
    EXPIRATION(4),
    REQUIREMENTS(5),
    PLACE(6),

    TRIAL(7),
    DANGER(8),
    ADDITIONAL_REWARD(9),
}

data class MedsTests (
    val id: String,
    val client: String,
    val reward: Int,
    val difficult: Float,
    val expiration: LocalDate,
    val requirements: String,
    val place: String,

    val trial: String,
    val danger: Int,
    val additionalReward: Int,
)