package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType

enum class ItemStoreCategory(val string: String) {
    UNAVAILABLE("Недоступен"),
    TECH("Технический"),
    MEDS("Медицинский"),
    INDUSTRIAL("Промышленый"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): ItemStoreCategory {
            return ItemStoreCategory.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

enum class ItemStoreType(
    val placeType: SpacePOIPlaceType,
    val categories: Array<ItemStoreCategory>,
    val level: Int
) {
    FUEL_DISPENSER(
        SpacePOIPlaceType.FUEL_DISPENSER,
        arrayOf(ItemStoreCategory.TECH),
        0),

    TECHNO_STORE(
        SpacePOIPlaceType.TECHNO_STORE,
        arrayOf(ItemStoreCategory.TECH),
        1),

    JPK_STORE(
        SpacePOIPlaceType.JPK_STORE,
        arrayOf(ItemStoreCategory.TECH),
        2),

    PHARMACY(
        SpacePOIPlaceType.PHARMACY,
        arrayOf(ItemStoreCategory.MEDS),
        1),

    XENOPHARM_STORE(SpacePOIPlaceType.XENOPHARM_STORE,
        arrayOf(ItemStoreCategory.MEDS),
        2),

    INDUSTRIAL_STORE(
        SpacePOIPlaceType.INDUSTRIAL_STORE,
        arrayOf(ItemStoreCategory.INDUSTRIAL),
        1),

    VERSEMINING_STORE(
        SpacePOIPlaceType.VERSEMINING_STORE,
        arrayOf(ItemStoreCategory.INDUSTRIAL),
        2),

    PICKAXE_STORE(
        SpacePOIPlaceType.PICKAXE_STORE,
        arrayOf(ItemStoreCategory.INDUSTRIAL),
        2);
}