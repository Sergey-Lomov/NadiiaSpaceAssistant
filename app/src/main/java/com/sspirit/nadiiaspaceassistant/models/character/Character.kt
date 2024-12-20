package com.sspirit.nadiiaspaceassistant.models.character

typealias CharacterRoutine = Array<CharacterRoutineItem>
typealias CharaterRoutinesMap = Map<CharacterSkillType, CharacterRoutine>

data class Character (
    val skills: Array<CharacterSkill>,
    val routines: CharaterRoutinesMap
) {
    companion object {
        fun emptyInstance() : Character {
            return Character(arrayOf(), mapOf())
        }
    }

    fun progress(type: CharacterSkillType): Int {
        return skills.first { it.type == type }.progress ?: 0
    }

    fun level(type: CharacterSkillType): Float {
        return progress(type).toFloat() / 10f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Character

        if (!skills.contentEquals(other.skills)) return false
        if (routines != other.routines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = skills.contentHashCode()
        result = 31 * result + routines.hashCode()
        return result
    }
}