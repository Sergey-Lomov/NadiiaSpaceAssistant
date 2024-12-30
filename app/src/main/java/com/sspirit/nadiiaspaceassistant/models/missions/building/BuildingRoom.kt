package com.sspirit.nadiiaspaceassistant.models.missions.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.SpecialLoot
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

private const val NO_ROOM_TYPE = "Нет"

enum class BuildingDevice(val string: String) {
    SAFETY_CONSOLE("Консоль безопасности"),
    SUPPORT_CONSOLE("Консоль жизнеобеспечения"),
    HOLO_PLAN("Голо-план"),
    ENERGY_NODE("Энергоузел"),
    ENERGY_CORE("Энергоядро (реактор)"),
    ACID_TANK("Резервуар кислоты"),
    MAINFRAME("Мэинфреим"),
    AUTO_DOCTOR("Автодоктор"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingDevice {
            return BuildingDevice.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class BuildingEvent(val string: String) {
    CABLES_FAIL("Выпадение кабелей"),
    CEIL_FAIL("Обвал покрытия"),
    HARD_CEIL_FAIL("Обвал тяжелого покрытия"),
    FLOOR_FAIL("Провал пола"),
    DEFENSE_TURRET("Активация турелей безопасности"),
    POISON_GAS("Ядовитые испарения"),
    PANIC_ATTACK("Панические атаки"),
    ACID_CONTAINER("Утечка кислоты (контейнер)"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingEvent {
            return BuildingEvent.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class BuildingRoom (
    val type: String,
    val location: BuildingLocation,
    val realLocation: RealLifeLocation,
    val light: Boolean,
    val loot: Array<LootGroupInstance>,
    val specLoot: Array<SpecialLoot>,
    val devices: Array<BuildingDevice>,
    val events: Array<BuildingEvent>,
) {
    val isValid: Boolean
        get() = type != NO_ROOM_TYPE

    val passages: Array<BuildingPassage>
        get() = location.passages
            .filter { it.room1 == this || it.room2 == this }
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

    val connectedRooms: Array<BuildingRoom>
        get() {
            val troughPassages = passages
                .filter { it.isPassable }
                .mapNotNull { it.anotherRoom(this) }
            val throughWalls = walls
                .filter { it.hasHole }
                .mapNotNull { it.anotherRoom(this) }
            return troughPassages
                .plus(throughWalls)
                .toTypedArray()
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