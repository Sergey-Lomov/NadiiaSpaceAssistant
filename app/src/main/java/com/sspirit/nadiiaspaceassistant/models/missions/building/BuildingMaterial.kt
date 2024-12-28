package com.sspirit.nadiiaspaceassistant.models.missions.building

enum class BuildingMaterialLucidity(val string: String) {
    OBVIOUS("Очевидный"),
    DEFAULT("Обычный"),
    UNCLEAR("Загадочный"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): BuildingMaterialLucidity {
            return entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class BuildingMaterial(
    val lucidity: BuildingMaterialLucidity,
    val heatImmune: Boolean,
    val acidImmune: Boolean,
    val explosionImmune: Boolean,
    ) {

    companion object {
        val default: BuildingMaterial = BuildingMaterial(
            lucidity = BuildingMaterialLucidity.DEFAULT,
            heatImmune = false,
            acidImmune = false,
            explosionImmune = false
        )

        val outer: BuildingMaterial = BuildingMaterial(
            lucidity = BuildingMaterialLucidity.OBVIOUS,
            heatImmune = true,
            acidImmune = true,
            explosionImmune = true
        )
    }

    val isDestructible: Boolean
        get() = !heatImmune || !acidImmune || !explosionImmune
}
