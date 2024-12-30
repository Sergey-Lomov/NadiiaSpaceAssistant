package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class BuildingPassagewayType(val string: String) {
    DOOR("Дверь"),
    SUPER_WALL("Стена"),
    OPEN_DOOR("Отркытая дверь"),
    HOLE("Дыра"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingPassagewayType {
            return BuildingPassagewayType.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class BuildingPassage (
    val room1: BuildingRoom,
    val room2: BuildingRoom,
    var type: BuildingPassagewayType,
    var door: BuildingDoor? = null,
    var vent: BuildingVent? = null
) {
    val location: BuildingLocation
        get() = room1.location

    val rooms: Array<BuildingRoom>
        get() = arrayOf(room1, room2)

    val realLocations: Array<RealLifeLocation>
        get() = rooms.map { it.realLocation }.toTypedArray()

    val isPassable: Boolean
        get() = type == BuildingPassagewayType.OPEN_DOOR || type == BuildingPassagewayType.HOLE

    fun isBetween(r1: RealLifeLocation, r2: RealLifeLocation): Boolean =
        r1 in realLocations && r2 in realLocations

    fun anotherRoom(room: BuildingRoom): BuildingRoom? =
        when (room) {
            room1 -> room2
            room2 -> room1
            else -> null
        }

    override fun hashCode(): Int {
        var result = room1.hashCode()
        result = 31 * result + room2.hashCode()
        return result
    }
}