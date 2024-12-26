package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

enum class BuildingDoorKeyCardColor {
    RED,
    GREEN,
    BLUE;

    override fun toString(): String {
        return when (this) {
            RED -> "красный"
            GREEN -> "зеленый"
            BLUE -> "синий"
        }
    }
}

data class BuildingDoorKeyCard (
    val color: BuildingDoorKeyCardColor
) : SpecialLoot {
    override val title: String
        get() = when (color) {
            BuildingDoorKeyCardColor.RED -> "Красная ключ-карта"
            BuildingDoorKeyCardColor.GREEN -> "Зеленая ключ-карта"
            BuildingDoorKeyCardColor.BLUE -> "Синяя ключ-карта"
        }
}
