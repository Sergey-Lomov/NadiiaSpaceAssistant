package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingSpecialLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

private const val NO_ROOM_TYPE = "Нет"

data class BuildingRoom (
    val type: String,
    val location: BuildingLocation,
    val realLocation: RealLifeLocation,
    val light: Boolean,
    var devices: Array<BuildingDevice>,
    var events: Array<BuildingEvent>,
) {
    val isValid: Boolean
        get() = type != NO_ROOM_TYPE

    val passages: Array<BuildingPassage>
        get() = location.passages
            .filter { it.room1 == this || it.room2 == this }
            .toTypedArray()

    val validPassages: Array<BuildingPassage>
        get() = passages
            .filter {
                it.type != BuildingPassagewayType.UNDEFINED
                        && it.type != BuildingPassagewayType.SUPER_WALL
            }
            .toTypedArray()

    val walls: Array<BuildingWall>
        get() = location.walls
            .filter { it.room1 == this || it.room2 == this }
            .toTypedArray()

    val ceiling: BuildingSlab
        get() {
            val slab = location.ceiling.firstOrNull { it.realLocation == this.realLocation }
            return slab ?: BuildingSlab.outer(location.sector, realLocation, location.ceilingLevel)
        }

    val floor: BuildingSlab
        get() {
            val slab = location.floor.firstOrNull { it.realLocation == this.realLocation }
            return slab ?: BuildingSlab.outer(location.sector, realLocation, location.floorLevel)
        }

    val slabs: Array<BuildingSlab>
        get() = arrayOf(ceiling, floor)

    val transports: Array<BuildingTransport>
        get() = location.sector.building.transports
            .filter { this in it.rooms }
            .toTypedArray()

    val bigObjects: Array<BuildingBigObject>
        get() = location.sector.building.bigObjects
            .filter { it.room == this }
            .toTypedArray()

    val loot: Array<BuildingLootContainer>
        get() = location.sector.building.loot
            .filter { it.room == this }
            .toTypedArray()

    val specLoot: Array<BuildingSpecialLootContainer>
        get() = location.sector.building.specLoot
            .filter { it.room == this }
            .toTypedArray()

    val connectedRooms: Array<BuildingRoom>
        get() {
            val troughPassages = passages
                .filter { it.isPassable }
                .map { it.anotherRoom(this) }
            val throughWalls = walls
                .filter { it.hasHole }
                .map { it.anotherRoom(this) }
            return troughPassages
                .plus(throughWalls)
                .toTypedArray()
        }

    val hasLadderHeap: Boolean
        get() {
            val total = bigObjects
                .filter { it.position == BuildingBigObjectPosition.Center }
                .sumOf { it.size }
            return total >= BuildingBigObject.CEILING_LADDER_SIZE
        }

    fun removeDevice(device: BuildingDevice) {
        devices = devices.filter { it != device }.toTypedArray()
    }

    fun addDevice(device: BuildingDevice) {
        val newDevices = devices.toMutableList()
        newDevices.add(device)
        devices = newDevices.toTypedArray()
    }

    fun removeEvent(event: BuildingEvent) {
        events = events.filter { it != event }.toTypedArray()
    }

    fun addEvent(event: BuildingEvent) {
        val newEvents = events.toMutableList()
        newEvents.add(event)
        events = newEvents.toTypedArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingRoom

        if (type != other.type) return false
        if (location.id != other.location.id) return false
        if (realLocation != other.realLocation) return false
        if (light != other.light) return false
        if (!loot.contentEquals(other.loot)) return false
        if (!specLoot.contentEquals(other.specLoot)) return false
        if (!devices.contentEquals(other.devices)) return false
        if (!events.contentEquals(other.events)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + location.id.hashCode()
        result = 31 * result + realLocation.hashCode()
        return result
    }
}