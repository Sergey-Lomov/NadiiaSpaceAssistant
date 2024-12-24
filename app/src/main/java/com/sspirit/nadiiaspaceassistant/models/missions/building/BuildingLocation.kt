package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class BuildingLocationType(val string: String) {
    LIVING_ROOMS("Жилые помещения"),
    WAREHOUSE("Склад"),
    MEDICINE("Мед. отсек"),
    CONTROL("Управление"),
    REACTOR("Реакторная"),
    HANGAR("Ангар"),
    BRIDGE("Рубка"),
    DATA_CENTER("Дата-центр"),
    LABORATORY("Лаборатория"),
    INDUSTRIAL_BAY("Машинный зал"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingLocationType {
            return BuildingLocationType.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class BuildingLocation (
    val id: String,
    val type: BuildingLocationType,
    val sector: BuildingSector,
    val level: Int,
    val title: String,
    var rooms: Array<BuildingRoom> = arrayOf(),
    var walls: Array<BuildingWall> = arrayOf(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingLocation

        if (id != other.id) return false
        if (type != other.type) return false
        if (sector != other.sector) return false
        if (level != other.level) return false
        if (title != other.title) return false
        if (!rooms.contentEquals(other.rooms)) return false
        if (!walls.contentEquals(other.walls)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + sector.hashCode()
        result = 31 * result + level
        result = 31 * result + title.hashCode()
        result = 31 * result + rooms.contentHashCode()
        result = 31 * result + walls.contentHashCode()
        return result
    }
}