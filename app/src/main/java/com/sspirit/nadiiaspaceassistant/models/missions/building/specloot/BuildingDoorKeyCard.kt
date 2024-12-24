package com.sspirit.nadiiaspaceassistant.models.missions.building.specloot

enum class BuildingDoorKeyCardColor {
    RED,
    GREEN,
    BLUE
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
