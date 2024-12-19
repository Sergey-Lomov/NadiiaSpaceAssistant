package com.sspirit.nadiiaspaceassistant.models.missions

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType

enum class MedsTestsProgressionKeys(override val index: Int) : IndexConvertibleKey {
    SKILL(0),
    TRIAL(1),
    L0(2),
    L1(3),
    L2(4),
    L3(5),
}

data class MedsTestsProgression(
    val skill: CharacterSkillType,
    val trial: String,
    val levels: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MedsTestsProgression

        if (skill != other.skill) return false
        if (trial != other.trial) return false
        if (!levels.contentEquals(other.levels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = skill.hashCode()
        result = 31 * result + trial.hashCode()
        result = 31 * result + levels.contentHashCode()
        return result
    }
}