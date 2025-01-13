package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType.UNDEFINED

enum class BuildingDoorKeyCardColor(val string: String) {
    RED("красный"),
    GREEN("зеленый"),
    BLUE("синий"),
    UNDEFINED("неопределено");

    companion object {
        fun byString(string: String): BuildingDoorKeyCardColor {
            return BuildingDoorKeyCardColor.entries.find { it.string == string } ?: UNDEFINED
        }
    }

    override fun toString(): String = string
}

data class BuildingDoorKeyCard (
    val color: BuildingDoorKeyCardColor
) : SpecialLoot {
    override val title: String
        get() = when (color) {
            BuildingDoorKeyCardColor.RED -> "Красная ключ-карта"
            BuildingDoorKeyCardColor.GREEN -> "Зеленая ключ-карта"
            BuildingDoorKeyCardColor.BLUE -> "Синяя ключ-карта"
            BuildingDoorKeyCardColor.UNDEFINED -> "Неопознанная ключ-карта"
        }
}
