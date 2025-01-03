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