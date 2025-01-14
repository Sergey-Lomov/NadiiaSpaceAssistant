package com.sspirit.nadiiaspaceassistant.models.character

import java.time.LocalDate

data class CharacterTrait (
    val id: String,
    val type: CharacterTraitType,
    val expiration: LocalDate?
) {
    val isExpired: Boolean
        get() = expiration?.isBefore(LocalDate.now()) ?: false

    fun mayAffect(skill: CharacterSkillType) : Boolean =
        type.effects.mayAffect(skill)

    fun effectOn(skill: CharacterSkillType) : Int =
        type.effectOn(skill)
}