package com.sspirit.nadiiaspaceassistant.models.cosmology

enum class SpacePOIPlaceType(val string: String, val isStore: Boolean = false) {
    FUEL_DISPENSER("Топливный автомат", true),
    TECHNO_STORE("Технический магазин", true),
    JPK_STORE("Магазин JPK Inc.", true),
    PHARMACY("Аптека", true),
    XENOPHARM_STORE("Магаизн XenoPharm", true),
    INDUSTRIAL_STORE("Пром-товары", true),
    VERSEMINING_STORE("Магазин Verse Mining", true),
    PICKAXE_STORE("Магазин Братства Кирки", true),
    QUANTUM_ARCHIVER("Квантовый архиватор"),
    WORKSHOP("Мастерская"),
    BUYUP("Скупка товаров"),
    SHOWROOM("Выставочный центр"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIPlaceType {
            return SpacePOIPlaceType.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class SpacePOIPlace(val parent: SpacePOI, val type: SpacePOIPlaceType) {
    val id: String
        get() = (parent.id + "_" + type.string).replace('-', '_')
}