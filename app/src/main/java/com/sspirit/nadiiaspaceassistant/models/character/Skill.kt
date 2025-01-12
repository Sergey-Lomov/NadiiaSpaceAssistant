package com.sspirit.nadiiaspaceassistant.models.character

import com.sspirit.nadiiaspaceassistant.utils.IndexConvertible

enum class CharacterSkillKeys(override val index: Int) : IndexConvertible {
    ID(0),
    TITLE(1),
    PROGRESS(2),
    ROUTINE(3),
}


enum class CharacterSkillType(val id: String, val restrictions: Array<CharacterSkillType> = arrayOf()) {
    PHYSIOLOGY("Ph"),
    MENTAL("Me"),
    FOOD("Fo"),
    POWER("Po", arrayOf(FOOD, PHYSIOLOGY)),
    AGILITY("Ag", arrayOf(FOOD, PHYSIOLOGY)),
    INTELLIGENCE("In", arrayOf(FOOD, MENTAL)),
    PILOTING("Pi"),
    COMMUNICATION("Co", arrayOf(MENTAL)),
    UNDEFINE("Undef");

    companion object {
        fun byId(id: String): CharacterSkillType {
            return entries.find { it.id == id } ?: UNDEFINE
        }
    }

    fun toId() : String {
        return id
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