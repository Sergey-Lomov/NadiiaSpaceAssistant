package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertible
import com.sspirit.nadiiaspaceassistant.extensions.random

enum class LootGroupLock(val string: String) {
    NONE("Нет"),
    PLAIN("Есть"),
    AUTO_LOCK("Самоблок"),
    UNDEFINED("Неизвестно");

    companion object {
        fun byString(string: String): LootGroupLock {
            return LootGroupLock.entries.find { it.string == string } ?: UNDEFINED
        }
    }
}

data class LootGroupItem(
    val descriptor: ItemDescriptor,
    val amount: IntRange,
    val weight: Float
)

data class LootGroup(
    val id: String,
    val title: String,
    val description: String,
    val lock: LootGroupLock,
    var items: MutableList<LootGroupItem> = mutableListOf()
) {
    fun generateItem(): ItemDescriptor {
        val weights = items.map { it.weight }.toTypedArray()
        return items.random(weights).descriptor
    }
}