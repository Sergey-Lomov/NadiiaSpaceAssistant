package com.sspirit.nadiiaspaceassistant.services.fabrics

import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import java.time.LocalDate
import java.util.UUID

object CharacterTraitsGenerator {

    fun oneDayLegInjury() : CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.LEG_INJURY,
            expiration = LocalDate.now().plusDays(1)
        )
    }
}