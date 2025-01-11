package com.sspirit.nadiiaspaceassistant.models.character

data class CharacterSkillEffect(
    val skill: CharacterSkillType,
    val effect: Int
) {
    companion object {
        fun power(effect: Int): CharacterSkillEffect =
            CharacterSkillEffect(CharacterSkillType.POWER, effect)

        fun agility(effect: Int): CharacterSkillEffect =
            CharacterSkillEffect(CharacterSkillType.AGILITY, effect)

        fun intelligent(effect: Int): CharacterSkillEffect =
            CharacterSkillEffect(CharacterSkillType.INTELLIGENCE, effect)
    }
}

fun Array<CharacterSkillEffect>.mayAffect(skill: CharacterSkillType) =
    any { it.skill == skill }

fun Array<CharacterSkillEffect>.affected(skill: CharacterSkillType, value: Int) : Int =
    fold(value) { acc, it ->
        if (it.skill == skill) acc + it.effect else acc
    }

fun Array<CharacterSkillEffect>.effectOn(skill: CharacterSkillType) : Int =
    filter { it.skill == skill }.sumOf { it.effect }