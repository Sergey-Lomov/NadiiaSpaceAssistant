package com.sspirit.nadiiaspaceassistant.models.character


import java.time.LocalDate

data class CharacterSkillEffect(
    val skill: CharacterSkillType,
    val effect: Int
)

data class CharacterTrait (
    val id: String,
    val title: String,
    val description: String,
    val skillEffects: Array<CharacterSkillEffect>,
    val expiration: LocalDate?
) {
    val isExpired: Boolean
        get() = expiration?.isBefore(LocalDate.now()) ?: false

    fun mayAffect(type: CharacterSkillType) : Boolean =
        skillEffects.any { it.skill == type }

    fun affected(type: CharacterSkillType, value: Int) : Int {
        return skillEffects.fold(value) { acc, it ->
            if (it.skill == type) acc + it.effect else acc
        }
    }

    fun effectOn(type: CharacterSkillType) : Int =
        skillEffects
            .filter { it.skill == type }
            .sumOf { it.effect }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterTrait

        if (id != other.id) return false
        if (title != other.title) return false
        if (description != other.description) return false
        if (!skillEffects.contentEquals(other.skillEffects)) return false
        if (expiration != other.expiration) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + skillEffects.contentHashCode()
        result = 31 * result + (expiration?.hashCode() ?: 0)
        return result
    }
}