package com.sspirit.nadiiaspaceassistant.services.fabrics

import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import com.sspirit.nadiiaspaceassistant.utils.plusDays
import java.time.LocalDate
import java.util.UUID

private const val SIDE_EFFECT_DURATION = 2

object CharacterTraitsGenerator {

    fun newTrait(type: CharacterTraitType, duration: Int): CharacterTrait {
        val expiration = if (duration != Int.MAX_VALUE)
            LocalDate.now().plusDays(duration)
        else
            null

        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = type,
            expiration = expiration
        )
    }

    fun sideEffectLethargic(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.LETHARGIC,
            expiration = LocalDate.now().plusDays(SIDE_EFFECT_DURATION)
        )
    }

    fun sideEffectWeakness(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.WEAKNESS,
            expiration = LocalDate.now().plusDays(SIDE_EFFECT_DURATION)
        )
    }

    fun sideEffectMigraine(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.MIGRAINE,
            expiration = LocalDate.now().plusDays(SIDE_EFFECT_DURATION)
        )
    }

    fun oneDayLegInjury(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.LEG_INJURY,
            expiration = LocalDate.now().plusDays(1)
        )
    }

    fun oneDayArmAcidBurn(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.ARM_ACID_BURN,
            expiration = LocalDate.now().plusDays(1)
        )
    }

    fun oneDayHeadGash(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.HEAD_GASH,
            expiration = LocalDate.now().plusDays(1)
        )
    }

    fun todayMildIntoxication(): CharacterTrait {
        return CharacterTrait(
            id = UUID.randomUUID().toString(),
            type = CharacterTraitType.MILD_INTOXICATION,
            expiration = LocalDate.now()
        )
    }
}