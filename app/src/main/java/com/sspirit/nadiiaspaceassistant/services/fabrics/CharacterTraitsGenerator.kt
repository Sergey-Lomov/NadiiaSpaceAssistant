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

    fun oneDayArmAcidBurn() : CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.ARM_ACID_BURN,
            expiration = LocalDate.now().plusDays(1)
        )
    }

    fun oneDayHeadGash() : CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.HEAD_GASH,
            expiration = LocalDate.now().plusDays(1)
        )
    }

    fun todayMildIntoxication() : CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.MILD_INTOXICATION,
            expiration = LocalDate.now()
        )
    }
}