package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation

import com.sspirit.nadiiaspaceassistant.extensions.readBoolean
import com.sspirit.nadiiaspaceassistant.extensions.readInt
import com.sspirit.nadiiaspaceassistant.extensions.readSplitedString
import com.sspirit.nadiiaspaceassistant.extensions.readString
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import kotlin.jvm.internal.Ref.IntRef

private val roomsLocations = mapOf(
    0 to RealLifeLocation.HALL,
    1 to RealLifeLocation.KITCHEN,
    2 to RealLifeLocation.PLAYROOM,
    3 to RealLifeLocation.BEDROOM,
    4 to RealLifeLocation.BATHROOM,
    5 to RealLifeLocation.TOILET,
)

private val passagesLocations = mapOf(
    0 to Pair(RealLifeLocation.HALL, RealLifeLocation.KITCHEN),
    1 to Pair(RealLifeLocation.HALL, RealLifeLocation.PLAYROOM),
    2 to Pair(RealLifeLocation.HALL, RealLifeLocation.BEDROOM),
    3 to Pair(RealLifeLocation.HALL, RealLifeLocation.BATHROOM),
    4 to Pair(RealLifeLocation.HALL, RealLifeLocation.TOILET),
)

private val wallsLocations = mapOf(
    0 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.BEDROOM),
    1 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.PLAYROOM),
    2 to Pair(RealLifeLocation.KITCHEN, RealLifeLocation.HALL),
    3 to Pair(RealLifeLocation.PLAYROOM, RealLifeLocation.HALL),
    4 to Pair(RealLifeLocation.PLAYROOM, RealLifeLocation.BATHROOM),
    5 to Pair(RealLifeLocation.BATHROOM, RealLifeLocation.TOILET),
)

private val floorsLocations = mapOf(
    0 to RealLifeLocation.HALL,
    1 to RealLifeLocation.KITCHEN,
    2 to RealLifeLocation.PLAYROOM,
    3 to RealLifeLocation.BEDROOM,
    4 to RealLifeLocation.BATHROOM,
    5 to RealLifeLocation.TOILET,
)

data class LocationTableRowMaterial(
    val lucidity: String,
    val heatImmune: Boolean,
    val acidImmune: Boolean,
    val explosionImmune: Boolean,
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowMaterial {
            return LocationTableRowMaterial(
                lucidity = raw.readString(displacement),
                heatImmune = raw.readBoolean(displacement),
                acidImmune = raw.readBoolean(displacement),
                explosionImmune = raw.readBoolean(displacement)
            )
        }
    }
}

data class LocationTableRowRoom(
    val type: String,
    val light: Boolean,
    val loot: String,
    val devices: Array<String>,
    val events: Array<String>
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowRoom {
            return LocationTableRowRoom(
                type = raw.readString(displacement),
                light = raw.readBoolean(displacement),
                loot = raw.readString(displacement),
                devices = raw.readSplitedString(displacement),
                events = raw.readSplitedString(displacement )
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationTableRowRoom

        if (type != other.type) return false
        if (light != other.light) return false
        if (loot != other.loot) return false
        if (!devices.contentEquals(other.devices)) return false
        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + light.hashCode()
        result = 31 * result + loot.hashCode()
        result = 31 * result + devices.contentHashCode()
        result = 31 * result + events.contentHashCode()
        return result
    }
}

data class LocationTableRowPassage(
    val type: String,
    val material: LocationTableRowMaterial,
    val locks: String,
    val hack: String,
    val turn: String,
    val vent: String,
    val ventState: String,
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowPassage {
            return LocationTableRowPassage(
                type = raw.readString(displacement),
                material = LocationTableRowMaterial.parse(raw, displacement),
                locks = raw.readString(displacement),
                hack = raw.readString(displacement),
                turn = raw.readString(displacement),
                vent = raw.readString(displacement),
                ventState = raw.readString(displacement)
            )
        }
    }
}

data class LocationTableRowWall(
    val material: LocationTableRowMaterial,
    val hasHole: Boolean
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowWall {
            return LocationTableRowWall(
                material = LocationTableRowMaterial.parse(raw, displacement),
                hasHole = raw.readBoolean(displacement),
            )
        }
    }
}

data class LocationTableRowFloor(
    val material: LocationTableRowMaterial,
    val hasHole: Boolean
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowFloor {
            return LocationTableRowFloor(
                material = LocationTableRowMaterial.parse(raw, displacement),
                hasHole = raw.readBoolean(displacement),
            )
        }
    }
}

typealias RealLifeLocations = Pair<RealLifeLocation, RealLifeLocation>

data class LocationTableRow(
    val id: String,
    val sector: String,
    val level: Int,
    val type: String,
    val title: String,
    val rooms: Map<RealLifeLocation, LocationTableRowRoom>,
    val passages: Map<RealLifeLocations, LocationTableRowPassage>,
    val walls: Map<RealLifeLocations, LocationTableRowWall>,
    val floors: Map<RealLifeLocation, LocationTableRowFloor>,
) {
    companion object {
        fun parse(raw: Array<Any>): LocationTableRow {
            val displacement = IntRef()
            return LocationTableRow(
                id = raw.readString(displacement),
                sector = raw.readString(displacement),
                level = raw.readInt(displacement),
                type = raw.readString(displacement),
                title = raw.readString(displacement),

                rooms = roomsLocations.keys
                    .sorted()
                    .associate { roomsLocations[it]!! to LocationTableRowRoom.parse(raw, displacement) },

                passages = passagesLocations.keys
                    .sorted()
                    .associate { passagesLocations[it]!! to LocationTableRowPassage.parse(raw, displacement) },

                walls = wallsLocations.keys
                    .sorted()
                    .associate { wallsLocations[it]!! to LocationTableRowWall.parse(raw, displacement) },

                floors = floorsLocations.keys
                    .sorted()
                    .associate { floorsLocations[it]!! to LocationTableRowFloor.parse(raw, displacement) }
            )
        }
    }
}