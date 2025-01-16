package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.utils.getBoolean
import com.sspirit.nadiiaspaceassistant.utils.getFloat
import com.sspirit.nadiiaspaceassistant.utils.getInt
import com.sspirit.nadiiaspaceassistant.utils.getNullableString
import com.sspirit.nadiiaspaceassistant.utils.getSplittedString
import com.sspirit.nadiiaspaceassistant.utils.getString
import com.sspirit.nadiiaspaceassistant.models.items.ItemBuyingSpec
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptorKeys
import com.sspirit.nadiiaspaceassistant.models.items.ItemLootCategory
import com.sspirit.nadiiaspaceassistant.models.items.ItemLootSpec
import com.sspirit.nadiiaspaceassistant.models.items.ItemStoreCategory
import com.sspirit.nadiiaspaceassistant.models.items.ItemStoreType
import java.time.LocalDateTime

private const val expirationHours = 2
private const val itemsSpreadsheetId = "14MXuy5wPFuFrsM8nYnFYw9lUFUErYR-BuEegsrVOTkA"
private const val itemsDescriptorsListRange = "Items!A3:Z100"

object ItemDataProvider : GoogleSheetDataProvider() {
    var descriptors: Array<ItemDescriptor> = arrayOf()

    fun downloadDescriptors(forced: Boolean = false) {
        if (expirationDate != null && !forced) {
            if (LocalDateTime.now() < expirationDate) {
                return
            }
        }

        val response = service
            .spreadsheets()
            .values()
            .get(itemsSpreadsheetId, itemsDescriptorsListRange)
            .execute()

        descriptors = parseToArray(response, "Item descriptors data invalid", ::parseDescriptor)
        expirationDate = LocalDateTime.now().plusHours(expirationHours.toLong())
    }

    fun getDescriptor(id: String) : ItemDescriptor? {
        return descriptors.firstOrNull { it.id == id }
    }
}

private fun parseDescriptor(raw: Array<Any>): ItemDescriptor {
    return ItemDescriptor(
        id =  raw.getString(ItemDescriptorKeys.ID),
        title = raw.getString(ItemDescriptorKeys.TITLE),
        isLocked = raw.getBoolean(ItemDescriptorKeys.IS_LOCKED, false),
        unlockRequirement = raw.getNullableString(ItemDescriptorKeys.UNLOCK_REQUIREMENT),
        buying = parseBuyingSpec(raw),
        sellPrice = raw.getInt(ItemDescriptorKeys.SELL_PRICE),
        loot = parseLootSpec(raw),
        showValue = raw.getInt(ItemDescriptorKeys.SHOW_VALUE)
    )
}

private fun parseLootSpec(raw: Array<Any>): ItemLootSpec {
    val rawCategories = raw.getSplittedString(ItemDescriptorKeys.LOOT_CATEGORIES, ",")
    val categories = rawCategories.map { ItemLootCategory.byString(it) }
    return ItemLootSpec(
        categories = categories.toTypedArray(),
        level = raw.getInt(ItemDescriptorKeys.LOOT_LEVEL),
        weight = raw.getFloat(ItemDescriptorKeys.LOOT_WEIGHT)
    )
}

private fun parseBuyingSpec(raw: Array<Any>): ItemBuyingSpec {
    val minAmount = raw.getInt(ItemDescriptorKeys.SHOP_MIN_AMOUNT)
    val maxAmount = raw.getInt(ItemDescriptorKeys.SHOP_MAX_AMOUNT)
    val minPrice = raw.getInt(ItemDescriptorKeys.SHOP_MIN_PRICE)
    val maxPrice = raw.getInt(ItemDescriptorKeys.SHOP_MAX_PRICE)

    val rawCategories = raw.getSplittedString(ItemDescriptorKeys.SHOP_CATEGORIES, ",")
    val categories = rawCategories.map { ItemStoreCategory.byString(it) }
    val shopLevel = raw.getInt(ItemDescriptorKeys.SHOP_LEVEL)
    val storesTypes = mutableListOf<ItemStoreType>()
    for (storeType in ItemStoreType.entries) {
        val validCategories = categories.any { it in storeType.categories }
        if (storeType.level >= shopLevel && validCategories)
            storesTypes.add(storeType)
    }

    return ItemBuyingSpec(
        storesTypes = storesTypes.toTypedArray(),
        amount = minAmount .. maxAmount,
        price = minPrice .. maxPrice,
        chance = raw.getFloat(ItemDescriptorKeys.SHOP_CHANCE),
        reputationRequirement = raw.getInt(ItemDescriptorKeys.SHOP_REP_REQUIREMENT)
    )
}