package com.sspirit.nadiiaspaceassistant.models.items

import com.sspirit.nadiiaspaceassistant.extensions.IndexConvertibleKey

enum class ItemDescriptorKeys(override val index: Int) : IndexConvertibleKey {
    ID(0),
    TITLE(1),
    SHOP_CATEGORIES(2),
    SHOP_LEVEL(3),
    SHOP_MIN_AMOUNT(4),
    SHOP_MAX_AMOUNT(5),
    SHOP_MIN_PRICE(6),
    SHOP_MAX_PRICE(7),
    SHOP_CHANCE(8),
    SHOP_REP_REQUIREMENT(9),
    SELL_PRICE(10),
    LOOT_CATEGORIES(11),
    LOOT_LEVEL(12),
    LOOT_WEIGHT(13),
    SHOW_VALUE(15),
    IS_LOCKED(16),
    UNLOCK_REQUIREMENT(17),
}

data class ItemDescriptor(
    val id: String,
    val title: String,
    val isLocked: Boolean,
    val unlockRequirement: String?,
    val buying: ItemBuyingSpec?,
    val sellPrice: Int,
    val loot: ItemLootSpec,
    val showValue: Int,
) {
    val isShowable: Boolean
        get() = showValue > 0
}