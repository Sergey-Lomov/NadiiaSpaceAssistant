package com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.location

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.LootGroupInstance
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot
import com.sspirit.nadiiaspaceassistant.utils.readBoolean
import com.sspirit.nadiiaspaceassistant.utils.readSplittedString
import com.sspirit.nadiiaspaceassistant.utils.readString
import com.sspirit.nadiiaspaceassistant.utils.write
import kotlin.jvm.internal.Ref.IntRef

data class LocationTableRowRoom(
    val type: String,
    val light: Boolean,
    val loot: String,
    val devices: Array<LocationTableRowDevice>,
    val events: Array<String>
) {
    companion object {
        fun parse(raw: Array<Any>, displacement: IntRef): LocationTableRowRoom {
            return LocationTableRowRoom(
                type = raw.readString(displacement),
                light = raw.readBoolean(displacement),
                loot = raw.readString(displacement),
                devices = LocationTableRowDevice.parseArray(raw, displacement),
                events = raw.readSplittedString(displacement )
            )
        }

        fun from(source: BuildingRoom) : LocationTableRowRoom {
            val devices = source.devices
                .map { LocationTableRowDevice.from(it) }
                .toTypedArray()
            val events = source.events
                .map { it.string }
                .toTypedArray()

            return LocationTableRowRoom(
                type = source.type,
                light = source.light,
                loot = encodeLoot(source.loot, source.specLoot),
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

    fun toBuildingRoom(location: BuildingLocation, realLocation: RealLifeLocation) : BuildingRoom {
        val devices = devices.map { it.toBuildingDevice() }.toTypedArray()
        val events = events.map { BuildingEvent.byString(it) }.toTypedArray()
        return BuildingRoom(
            type = type,
            location = location,
            realLocation = realLocation,
            light = light,
            loot = decodeLoot(loot),
            specLoot = decodeSpecLoot(loot),
            devices = devices,
            events = events
        )
    }
}

private fun decodeSpecLoot(rawLoot: String) : Array<SpecialLoot> {
    return arrayOf()
}

private fun decodeLoot(rawLoot: String) : Array<LootGroupInstance> {
    return arrayOf()
}

private fun encodeLoot(common: Array<LootGroupInstance>, spec: Array<SpecialLoot>) : String {
    return ""
}

fun MutableList<String>.write(room: LocationTableRowRoom) {
    write(room.type)
    write(room.light)
    write(room.loot)
    write(room.devices)
    write(room.events)
}