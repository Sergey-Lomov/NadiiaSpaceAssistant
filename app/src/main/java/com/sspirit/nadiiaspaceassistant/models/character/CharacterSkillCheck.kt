package com.sspirit.nadiiaspaceassistant.models.character

data class CharacterSkillCheck(
    val skill: CharacterSkillType,
    val isUnexpected: Boolean,
    val requirement: Int,
    val accuracy: Int
)