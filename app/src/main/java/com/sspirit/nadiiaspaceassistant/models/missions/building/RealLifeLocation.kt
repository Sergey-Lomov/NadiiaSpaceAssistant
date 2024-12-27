package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class RealLifeLocation(val string: String) {
    HALL("Холл"),
    KITCHEN("Кухня"),
    PLAYROOM("Детская"),
    BEDROOM("Спальня"),
    BATHROOM("Ванная"),
    TOILET("Туалет"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): RealLifeLocation {
            return RealLifeLocation.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    override fun toString(): String {
        return string
    }
}