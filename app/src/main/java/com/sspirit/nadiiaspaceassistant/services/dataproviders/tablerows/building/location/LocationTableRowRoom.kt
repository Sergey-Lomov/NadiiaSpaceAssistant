package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.building.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.readSplitString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class LocationTableRowRoom(
    val type: String,
    val light: Boolean,
    val devices: Array<LocationTableRowDevice>,
    val events: Array<String>
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowRoom {
            return LocationTableRowRoom(
                type = raw.readString(displacement),
                light = raw.readBoolean(displacement),
                devices = LocationTableRowDevice.parseArray(raw, displacement),
                events = raw.readSplitString(displacement )
            )
        }

        fun from(source: BuildingRoom) : LocationTableRowRoom {
            val devices = source.devices
                .map { LocationTableRowDevice.from(it) }
                .toTypedArray()
            val events = source.events
                .map { it.title }
                .toTypedArray()

            return LocationTableRowRoom(
                type = source.type,
                light = source.light,
                devices = devices,
                events = events
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationTableRowRoom

        if (type != other.type) return false
        if (light != other.light) return false
        if (!devices.contentEquals(other.devices)) return false
        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + light.hashCode()
        result = 31 * result + devices.contentHashCode()
        result = 31 * result + events.contentHashCode()
        return result
    }

    fun toBuildingRoom(location: BuildingLocation, realLocation: RealLifeLocation) : BuildingRoom {
        val devices = devices.map { it.toBuildingDevice() }.toTypedArray()
        val events = events.map { BuildingEvent.byString(it) }.toTypedArray()
        return BuildingRoom(
            type = type,
            location = location,
            realLocation = realLocation,
            light = light,
            devices = devices,
            events = events
        )
    }
}

fun MutableList<String>.write(room: LocationTableRowRoom) {
    write(room.type)
    write(room.light)
    write(room.devices)
    write(room.events)
}