package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillEffect
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import com.sspirit.nadiiaspaceassistant.utils.format
import com.sspirit.nadiiaspaceassistant.utils.plusDays
import com.sspirit.nadiiaspaceassistant.utils.readDate
import com.sspirit.nadiiaspaceassistant.utils.readOptionalDate
import com.sspirit.nadiiaspaceassistant.utils.readSplitString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.toSignedString
import com.sspirit.nadiiaspaceassistant.utils.write
import com.sspirit.nadiiaspaceassistant.utils.writeOptional
import java.time.LocalDate
import kotlin.jvm.internal.Ref.IntRef

private const val REMOVING_DATE_GAP = 1

data class CharacterTraitRow (
    val id: String,
    val title: String,
    val description: String,
    val skillEffects: Array<String>,
    val expiration: LocalDate?
) : RawDataConvertibleTableRow {
    companion object {
        fun parse(raw: Array<Any>): CharacterTraitRow {
            val ref = IntRef()
            return CharacterTraitRow(
                id = raw.readString(ref),
                title = raw.readString(ref),
                description = raw.readString(ref),
                skillEffects = raw.readSplitString(ref),
                expiration = raw.readOptionalDate(ref)
            )
        }

        fun from(source: CharacterTrait): CharacterTraitRow {
            return CharacterTraitRow(
                id = source.id,
                title = source.type.title,
                description = source.type.info,
                skillEffects = encodeEffects(source.type.effects),
                expiration = source.expiration
            )
        }
    }

    fun toTrait(): CharacterTrait =
        CharacterTrait(
            id = id,
            type = CharacterTraitType.byString(title),
            expiration = expiration
        )

    override fun toRawData(): List<String> {
        val data = mutableListOf<String>()
        data.write(id)
        data.write(title)
        data.write(description)
        data.write(skillEffects)
        data.writeOptional(expiration)
        data.writeOptional(expiration?.plusDays(REMOVING_DATE_GAP))
        return data
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CharacterTraitRow

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
        result = 31 * result + expiration.hashCode()
        return result
    }
}

private fun encodeEffects(effects: Array<CharacterSkillEffect>): Array<String> =
    effects
        .map { "${it.skill.id} ${it.effect.toSignedString()}" }
        .toTypedArray()

private fun decodeEffects(strings: Array<String>): Array<CharacterSkillEffect> =
    strings
        .map {
            val parts = it.split(" ")
            if (parts.size < 2) return@map null
            return@map CharacterSkillEffect(
                skill = CharacterSkillType.byId(parts[0]),
                effect = parts[1].toInt()
            )
        }
        .filterNotNull()
        .toTypedArray()