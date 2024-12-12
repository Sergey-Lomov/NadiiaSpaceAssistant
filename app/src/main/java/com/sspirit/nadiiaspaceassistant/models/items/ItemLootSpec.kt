package com.sspirit.nadiiaspaceassistant.models.items

import android.util.Range

enum class ItemLootCategory(val string: String) {
    UNAVAILABLE("Недоступен"),
    FUEL("Топливо"),
    FUEL_TECH("Топливные технологии"),
    COSMONAVIGATION("Космонавигация"),
    HYPERNAVIGATION("Гипернавигация"),
    PHARMACY("Медицина"),
    INDUSTRIAL("Промышленый"),
    STORY("Сюжетное"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): ItemLootCategory {
            return ItemLootCategory.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class ItemLootSpec(
    val categories: Array<ItemLootCategory>,
    val level: Int,
    val weight: Float,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemLootSpec

        if (!categories.contentEquals(other.categories)) return false
        if (level != other.level) return false
        if (weight != other.weight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categories.contentHashCode()
        result = 31 * result + level
        result = 31 * result + weight.hashCode()
        return result
    }
}