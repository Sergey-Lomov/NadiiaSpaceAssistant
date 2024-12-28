package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorLock
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassageway
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize
import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

private const val NO_VENT = "Нет"

data class LocationTableRowPassage(
    val type: String,
    val material: LocationTableRowMaterial,
    val locks: Array<String>,
    val hack: String,
    val turn: String,
    val ventSize: String,
    val ventGrille: String,
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowPassage {
            return LocationTableRowPassage(
                type = raw.readString(displacement),
                material = LocationTableRowMaterial.parse(raw, displacement),
                locks = raw.readSplittedString(displacement),
                hack = raw.readString(displacement),
                turn = raw.readString(displacement),
                ventSize = raw.readString(displacement),
                ventGrille = raw.readString(displacement)
            )
        }

        fun from(source: BuildingPassageway) : LocationTableRowPassage {
            val material = source.door?.material ?: BuildingMaterial.default
            val locks = source.door?.locks
                ?.map { it.toString() }
                ?.toTypedArray()
                ?: arrayOf()
            val hack = source.door?.hacking?.string ?: BuildingDoorHackingLevel.UNDEFINED.string
            val turn = source.door?.turn?.string ?: BuildingDoorTurn.UNDEFINED.string
            val ventSize = source.vent?.size?.string ?: NO_VENT
            val ventGrille = source.vent?.grilleState?.string ?: BuildingVentGrilleState.UNDEFINED.string

            return LocationTableRowPassage(
                type = source.type.string,
                material = LocationTableRowMaterial.from(material),
                locks = locks,
                hack = hack,
                turn = turn,
                ventSize = ventSize,
                ventGrille = ventGrille
            )
        }
    }

    fun toBuildingPassage(r1: BuildingRoom, r2: BuildingRoom) : BuildingPassageway {
        val locks = locks.map { BuildingDoorLock.byString(it) }.toTypedArray()

        val passage = BuildingPassageway(
            room1 = r1,
            room2 = r2,
            type = BuildingPassagewayType.byString(type)
        )

        if (passage.type == BuildingPassagewayType.OPEN_DOOR || passage.type == BuildingPassagewayType.DOOR) {
            passage.door = BuildingDoor(
                passageway = passage,
                locks = locks,
                hacking = BuildingDoorHackingLevel.byString(hack),
                turn = BuildingDoorTurn.byString(turn),
                material = material.toBuildingMaterial()
            )
        }

        if (ventSize != NO_VENT) {
            passage.vent = BuildingVent(
                passageway = passage,
                size = BuildingVentSize.byString(ventSize),
                grilleState = BuildingVentGrilleState.byString(ventGrille)
            )
        }

        return passage
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationTableRowPassage

        if (type != other.type) return false
        if (material != other.material) return false
        if (!locks.contentEquals(other.locks)) return false
        if (hack != other.hack) return false
        if (turn != other.turn) return false
        if (ventSize != other.ventSize) return false
        if (ventGrille != other.ventGrille) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + material.hashCode()
        result = 31 * result + locks.contentHashCode()
        result = 31 * result + hack.hashCode()
        result = 31 * result + turn.hashCode()
        result = 31 * result + ventSize.hashCode()
        result = 31 * result + ventGrille.hashCode()
        return result
    }
}

fun MutableList<String>.write(passage: LocationTableRowPassage) {
    write(passage.type)
    write(passage.material)
    write(passage.locks)
    write(passage.hack)
    write(passage.turn)
    write(passage.ventSize)
    write(passage.ventGrille)
}