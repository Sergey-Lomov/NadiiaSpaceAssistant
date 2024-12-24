package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class BuildingMaterialLucidity(val string: String) {
    OBVIOUS("Очевидный"),
    DEFAULT("Обычный"),
    UNCLEAR("Загадочный"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingMaterialLucidity {
            return BuildingMaterialLucidity.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}


data class BuildingMaterial(
    val lucidity: BuildingMaterialLucidity,
    val heatImmune: Boolean,
    val acidImmune: Boolean,
    val explosionImmune: Boolean
)