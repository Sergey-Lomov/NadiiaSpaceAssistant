package com.sspirit.nadiiaspaceassistant.models.cosmology

import com.sspirit.nadiiaspaceassistant.utils.IdEquatableEntity

enum class SpacePOIPlaceType(val title: String, val isStore: Boolean = false) {
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
    HOSPITAL("Больница"),
    BUYUP("Скупка товаров"),
    SHOWROOM("Выставочный центр"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): SpacePOIPlaceType {
            return SpacePOIPlaceType.entries.find { it.title == string } ?: UNDEFINED
        }
    }
}

data class SpacePOIPlace (
    val parent: SpacePOI,
    val type: SpacePOIPlaceType
) : IdEquatableEntity() {
    override val id: String
        get() = (parent.id + "_" + type.title).replace('-', '_')

    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()
}