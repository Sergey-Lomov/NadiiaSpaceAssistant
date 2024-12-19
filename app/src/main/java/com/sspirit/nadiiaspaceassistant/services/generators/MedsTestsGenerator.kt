package com.sspirit.nadiiaspaceassistant.services.generators

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.MedsTests
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsPreviewsDataProvider
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

private val baseRewardRange = 40..60
private val additionalReward = 9..13
private const val progressionStep = 15f
private const val maxProgressionDifficult = 45f
private const val maxProgressionLevel = 3
private const val requirementsGap = 5

fun generateMedsTest(): MedsTests {
    val skills = arrayOf(CharacterSkillType.POWER, CharacterSkillType.INTELLIGENCE, CharacterSkillType.AGILITY)
    val skill = skills.random()
    val progressions = MedsTestsDataProvider.progressions.filter { it.skill == skill }
    val progression = progressions.random()
    val progressionMult = 0.85f + 0.3f * Random.nextFloat()
    val progressionDifficult = MissionsPreviewsDataProvider.progressionDifficult * progressionMult
    val progressLevel = (progressionDifficult / progressionStep).toInt().coerceAtMost(maxProgressionLevel)
    val value = progression.levels[progressLevel]
    val trial = "${progression.trial}: $value"
    val difficult = (progressionDifficult / maxProgressionDifficult).coerceAtMost(1f)
    val danger = (difficult * CharacterDataProvider.MAX_SKILL_PROGRESS).toInt()
    val expireDays = arrayOf(1, 2, 3).random().toLong()
    val expiration = LocalDate.now().plusDays(expireDays)
    val skillTitle = CharacterDataProvider.character.skills.first { it.type == skill }.title
    val skillRequirement = (danger - requirementsGap).coerceIn(0, CharacterDataProvider.MAX_SKILL_PROGRESS)
    val requirements = "$skillTitle: $skillRequirement"

    val client = when (Random.nextFloat()) {
        in 0.0 .. 0.1 -> "Health++"
        in 0.1 .. 0.2 -> "Tricky Pills"
        in 0.2 .. 0.3 -> "Rainbow Inc."
        else -> "XenoPharm"
    }

    return MedsTests(
        id = UUID.randomUUID().toString(),
        client = client,
        trial = trial,
        reward = baseRewardRange.random(),
        difficult = difficult,
        danger = danger,
        additionalReward = additionalReward.random(),
        expiration = expiration,
        requirements = requirements
    )
}
