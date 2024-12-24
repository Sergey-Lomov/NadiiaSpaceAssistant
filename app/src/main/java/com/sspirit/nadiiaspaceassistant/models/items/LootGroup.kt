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

enum class LootGroupKeys(override val index: Int) : IndexConvertible {
    ID(0),
    TITLE(1),
    DESCRIPTION(2),
    LOCK(3),
    ITEM_ID(4),
    ITEM_MIN_AMOUNT(5),
    ITEM_MAX_AMOUNT(6),
    ITEM_WEIGHT(7)
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
    var items: MutableList<LootGroupItem>
) {
    fun generateItem(): ItemDescriptor {
        val weights = items.map { it.weight }.toTypedArray()
        return items.random(weights).descriptor
    }
}