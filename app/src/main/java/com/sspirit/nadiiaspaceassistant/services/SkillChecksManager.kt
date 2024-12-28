package com.sspirit.nadiiaspaceassistant.services

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.SkillCheck
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import java.util.UUID
import kotlin.math.max

private const val maxSize = 50

private data class SkillCheckRegistration(
    val check: SkillCheck,
    val id: String = UUID.randomUUID().toString(),
)

object SkillChecksManager {
    private val checks: MutableList<SkillCheckRegistration> = mutableListOf()

    fun get(id: String): SkillCheck? =
        checks.firstOrNull { it.id == id }?.check

    private fun register(check: SkillCheck): String {
        if (checks.size >= maxSize) checks.removeAt(0)
        val registration = SkillCheckRegistration(check)
        checks.add(registration)
        return registration.id
    }

    fun registerDoorOpenCheck(door: BuildingDoor): String {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (door.turn) {
            BuildingDoorTurn.EASY -> difficult + 6 - 3
            BuildingDoorTurn.MEDIUM -> difficult + 6
            BuildingDoorTurn.HARD -> difficult + 6 + 3
            BuildingDoorTurn.UNDEFINED,
            BuildingDoorTurn.AUTOMATIC,
            BuildingDoorTurn.BROKEN -> 0
        }

        val check = SkillCheck(
            skill = CharacterSkillType.POWER,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )

        return register(check)
    }

    fun registerDoorHackCheck(door: BuildingDoor): String {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (door.hacking) {
            BuildingDoorHackingLevel.UNHACKABLE -> CharacterDataProvider.MAX_SKILL_PROGRESS * 2
            BuildingDoorHackingLevel.EASY -> difficult + 6 - 3
            BuildingDoorHackingLevel.MEDIUM -> difficult + 6
            BuildingDoorHackingLevel.HARD -> difficult + 6 + 3
            BuildingDoorHackingLevel.UNDEFINED -> 0
        }

        val check = SkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )

        return register(check)
    }

    fun registerVentCrawlCheck(vent: BuildingVent): String {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (vent.size) {
            BuildingVentSize.MINIMAL -> CharacterDataProvider.MAX_SKILL_PROGRESS * 2
            BuildingVentSize.NARROW -> difficult + 6 - 3
            BuildingVentSize.STANDARD -> difficult + 6
            BuildingVentSize.WIDE -> difficult + 6 + 3
            BuildingVentSize.UNDEFINED -> 0
        }

        val check = SkillCheck(
            skill = CharacterSkillType.AGILITY,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )

        return register(check)
    }
}