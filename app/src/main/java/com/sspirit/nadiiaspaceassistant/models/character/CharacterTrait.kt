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
        type.effects.any { it.skill == skill }

    fun affected(skill: CharacterSkillType, value: Int) : Int {
        return type.effects.fold(value) { acc, it ->
            if (it.skill == skill) acc + it.effect else acc
        }
    }

    fun effectOn(skill: CharacterSkillType) : Int =
        type.effects
            .filter { it.skill == skill }
            .sumOf { it.effect }
}