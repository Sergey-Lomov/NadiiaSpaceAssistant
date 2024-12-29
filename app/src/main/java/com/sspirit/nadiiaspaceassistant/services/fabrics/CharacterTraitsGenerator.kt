package com.sspirit.nadiiaspaceassistant.services.fabrics

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillEffect
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import java.time.LocalDate
import java.util.UUID

object CharacterTraitsGenerator {
    const val LEG_INJURY_TITLE = "Ушиб ноги"

    fun legInjury() : CharacterTrait {
        val effect = CharacterSkillEffect(CharacterSkillType.AGILITY, -5)
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            title = LEG_INJURY_TITLE,
            description = "Ловкость -5, запрещено спрыгивать в дыры.",
            skillEffects = arrayOf(effect),
            expiration = LocalDate.now().plusDays(1)
        )
    }
}