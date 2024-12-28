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

data class BuildingPassageway (
    val room1: BuildingRoom,
    val room2: BuildingRoom,
    var type: BuildingPassagewayType,
    var door: BuildingDoor? = null,
    var vent: BuildingVent? = null
) {
    val location: BuildingLocation
        get() = room1.location

    override fun hashCode(): Int {
        var result = room1.hashCode()
        result = 31 * result + room2.hashCode()
        return result
    }
}