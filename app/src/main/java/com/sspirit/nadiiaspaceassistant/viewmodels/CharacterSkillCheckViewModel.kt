package com.sspirit.nadiiaspaceassistant.viewmodels

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillCheck

data class CharacterSkillCheckViewModel(
    val check: CharacterSkillCheck,
    val onSuccess: () -> Unit,
    val onFail: () -> Unit
)