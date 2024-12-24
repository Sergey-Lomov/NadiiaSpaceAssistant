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
    val type: BuildingPassagewayType,
    val door: BuildingDoor?,
    val vent: BuildingVent?
)