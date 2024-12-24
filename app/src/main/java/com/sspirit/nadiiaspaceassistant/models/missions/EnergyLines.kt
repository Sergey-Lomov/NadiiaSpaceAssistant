package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertible
import java.time.LocalDate

enum class EnergyLinesKeys(override val index: Int) : IndexConvertible {
    ID(0),
    CLIENT(1),
    REWARD(2),
    DIFFICULT(3),
    EXPIRATION(4),
    REQUIREMENTS(5),
    PLACE(6),

    TIME_MULT(7),
    LENGTH_MULT(8),
    VALUES(9),
    RULES(10),
    LANDING_INFO(11),
    HARD_PLACES(12),
    LIGHT(13),
}

data class EnergyLines(
    val id: String,
    val client: String,
    val reward: Int,
    val difficult: Float,
    val expiration: LocalDate,
    val requirements: String,
    val place: String,

    val landingTimeMult: Float,
    val landingLengthMult: Float,
    val values: Array<String>,
    val rules: Array<String>,
    val landingInfo: String,
    val hardPlaces: Boolean,
    val light: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnergyLines

        if (id != other.id) return false
        if (client != other.client) return false
        if (reward != other.reward) return false
        if (difficult != other.difficult) return false
        if (expiration != other.expiration) return false
        if (requirements != other.requirements) return false
        if (place != other.place) return false
        if (landingTimeMult != other.landingTimeMult) return false
        if (landingLengthMult != other.landingLengthMult) return false
        if (!rules.contentEquals(other.rules)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + client.hashCode()
        result = 31 * result + reward
        result = 31 * result + difficult.hashCode()
        result = 31 * result + expiration.hashCode()
        result = 31 * result + requirements.hashCode()
        result = 31 * result + place.hashCode()
        result = 31 * result + landingTimeMult.hashCode()
        result = 31 * result + landingLengthMult.hashCode()
        result = 31 * result + rules.contentHashCode()
        return result
    }
}