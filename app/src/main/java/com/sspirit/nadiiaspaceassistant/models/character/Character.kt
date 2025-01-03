package com.sspirit.nadiiaspaceassistant.models.character

typealias CharacterRoutine = Array<CharacterRoutineItem>
typealias CharaterRoutinesMap = Map<CharacterSkillType, CharacterRoutine>

data class Character (
    val skills: Array<CharacterSkill>,
    val routines: CharaterRoutinesMap,
    val traits: MutableList<CharacterTrait> = mutableListOf()
) {
    companion object {
        fun emptyInstance() : Character {
            return Character(arrayOf(), mapOf())
        }
    }

    fun pureProgress(type: CharacterSkillType): Int {
        return skills.first { it.type == type }.progress ?: 0
    }

    fun progress(type: CharacterSkillType): Int {
        return traitBySkill(type)
            .fold(pureProgress(type)) { acc, it ->
                it.affected(type, acc)
            }
    }

    fun level(type: CharacterSkillType): Float {
        return progress(type).toFloat() / 10f
    }

    fun skill(type: CharacterSkillType) : CharacterSkill = skills.first { it.type == type }

    fun hasTraitType(type: CharacterTraitType) : Boolean =
        traits.any { it.type == type }

    fun hasTrait(trait: CharacterTrait) : Boolean =
        traits.any { it == trait }

    fun traitBySkill(type: CharacterSkillType) : Array<CharacterTrait> = traits
        .filter { it.mayAffect(type) }
        .toTypedArray()

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