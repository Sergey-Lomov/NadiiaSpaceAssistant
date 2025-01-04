package com.sspirit.nadiiaspaceassistant.services

import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillCheck
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider


object SkillChecksManager {

    fun openDoor(door: BuildingDoor): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (door.turn) {
            BuildingDoorTurn.EASY -> difficult + 6 - 3
            BuildingDoorTurn.MEDIUM -> difficult + 6
            BuildingDoorTurn.HARD -> difficult + 6 + 3
            BuildingDoorTurn.UNDEFINED,
            BuildingDoorTurn.AUTOMATIC,
            BuildingDoorTurn.BROKEN -> 0
        }

        return CharacterSkillCheck(
            skill = CharacterSkillType.POWER,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )
    }

    fun hackDoor(door: BuildingDoor): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (door.hacking) {
            BuildingDoorHackingLevel.UNHACKABLE -> CharacterDataProvider.MAX_SKILL_PROGRESS * 2
            BuildingDoorHackingLevel.EASY -> difficult + 6 - 3
            BuildingDoorHackingLevel.MEDIUM -> difficult + 6
            BuildingDoorHackingLevel.HARD -> difficult + 6 + 3
            BuildingDoorHackingLevel.UNDEFINED -> 0
        }

        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )
    }

    fun crawlVent(vent: BuildingVent): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        val requirement = when (vent.size) {
            BuildingVentSize.MINIMAL -> CharacterDataProvider.MAX_SKILL_PROGRESS * 2
            BuildingVentSize.NARROW -> difficult + 6 - 3
            BuildingVentSize.STANDARD -> difficult + 6
            BuildingVentSize.WIDE -> difficult + 6 + 3
            BuildingVentSize.UNDEFINED -> 0
        }

        return CharacterSkillCheck(
            skill = CharacterSkillType.AGILITY,
            isUnexpected = false,
            requirement = requirement,
            accuracy = 12
        )
    }

    fun jumpIntoHole(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.AGILITY,
            isUnexpected = false,
            requirement = difficult + 4,
            accuracy = 12
        )
    }

    fun hackSafetyConsole(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = false,
            requirement = difficult + 9,
            accuracy = 12
        )
    }

    fun optimizeEnergyNode(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = false,
            requirement = difficult + 3,
            accuracy = 12
        )
    }

    fun searchGoalData(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = false,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun cablesFallEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.AGILITY,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun ceilingFallEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.POWER,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun floorFallEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.AGILITY,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun defendsTurretEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun poisonGasEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.PHYSIOLOGY,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 12
        )
    }

    fun panicAttackEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.MENTAL,
            isUnexpected = true,
            requirement = difficult + 8,
            accuracy = 12
        )
    }

    fun acidContainerEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = true,
            requirement = difficult + 6,
            accuracy = 10
        )
    }

    fun engineerEpiphanyEvent(): CharacterSkillCheck {
        val difficult = MissionsListDataProvider.progressionDifficult
        return CharacterSkillCheck(
            skill = CharacterSkillType.INTELLIGENCE,
            isUnexpected = true,
            requirement = difficult + 3,
            accuracy = 6
        )
    }
}