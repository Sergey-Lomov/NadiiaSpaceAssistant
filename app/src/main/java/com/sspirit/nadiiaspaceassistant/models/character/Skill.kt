package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey

enum class CharacterSkillKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    TITLE(1),
    PROGRESS(2),
    ROUTINE(3),
}

enum class CharacterSkillType {
    PHYSIOLOGY,
    MENTAL,
    FOOD,
    POWER,
    AGILITY,
    INTELLIGENCE,
    PILOTING,
    COMMUNICATION,
    UNDEFINE;

    companion object {
        fun byId(id: String): CharacterSkillType {
            return when (id) {
                "Ph" -> PHYSIOLOGY
                "Me" -> MENTAL
                "Fo" -> FOOD
                "Po" -> POWER
                "Ag" -> AGILITY
                "In" -> INTELLIGENCE
                "Pi" -> PILOTING
                "Co" -> COMMUNICATION
                else -> UNDEFINE
            }
        }
    }
}

data class CharacterSkill(
    val type: CharacterSkillType,
    val title: String,
    var progress: Int
) {

    val level: Int
        get() = progress / 10
}