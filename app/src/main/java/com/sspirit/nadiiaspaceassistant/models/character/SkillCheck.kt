package com.sspirit.nadiiaspaceassistant.models.character

import java.util.UUID

data class SkillCheck(
    val skill: CharacterSkillType,
    val isUnexpected: Boolean,
    val requirement: Int,
    val accuracy: Int
)