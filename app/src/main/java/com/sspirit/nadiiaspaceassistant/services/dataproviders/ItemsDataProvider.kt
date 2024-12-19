package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.sspirit.nadiiaspaceassistant.extensions.getBoolean
import com.sspirit.nadiiaspaceassistant.extensions.getDate
import com.sspirit.nadiiaspaceassistant.extensions.getFloat
import com.sspirit.nadiiaspaceassistant.extensions.getInt
import com.sspirit.nadiiaspaceassistant.extensions.getNullableString
import com.sspirit.nadiiaspaceassistant.extensions.getSplitedString
import com.sspirit.nadiiaspaceassistant.extensions.getString
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.ItemBuyingSpec
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptorKeys
import com.sspirit.nadiiaspaceassistant.models.items.ItemLootCategory
import com.sspirit.nadiiaspaceassistant.models.items.ItemLootSpec
import com.sspirit.nadiiaspaceassistant.models.items.ItemStoreCategory
import com.sspirit.nadiiaspaceassistant.models.items.ItemStoreType
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockListItem
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemKeys
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemPredeterminationKeys
import com.sspirit.nadiiaspaceassistant.services.generators.generateStockList
import java.time.LocalDateTime

private const val expirationHours = 2
private const val itemsSpreadsheetId = "14MXuy5wPFuFrsM8nYnFYw9lUFUErYR-BuEegsrVOTkA"
private const val shopsSpreadsheetId = "1wlzHqYjTI68koZbLcIRRtOayXpeuMYxrRhtrjwsMh8s"
private const val predeterminationsRange = "Orders!A1:H100"
private const val itemsDescriptorsListRange = "Items!A3:Z100"
private const val stockListRange = "!A1:E50"
private const val stockAmountColumn = "B"

object ItemDataProvider : GoogleSheetDataProvider() {
    private var stocks = mutableMapOf<String, StockList>()
    var descriptors: Array<ItemDescriptor> = arrayOf()

    fun getDescriptors(forced: Boolean = false) {
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

    private fun getPredeterminations(): Array<StockListItemPredetermination> {
        val response = service
            .spreadsheets()
            .values()
            .get(shopsSpreadsheetId, predeterminationsRange)
            .execute()

        return parseToArray(response, "Stock item predetermination data invalid", ::parsePredetermination)
    }

    private fun parsePredetermination(raw: Array<Any>): StockListItemPredetermination {
        val id = raw.getString(StockListItemPredeterminationKeys.ITEM_ID)
        val descriptor = descriptors.first { it.id == id }
        val item = StockListItem(
            descriptor = descriptor,
            amount = raw.getInt(StockListItemPredeterminationKeys.ITEM_AMOUNT),
            price = raw.getInt(StockListItemPredeterminationKeys.ITEM_PRICE),
            isPreOrder = raw.getBoolean(StockListItemPredeterminationKeys.IS_PREORDER, false)
        )

        val from = raw.getDate(StockListItemPredeterminationKeys.FROM_DATE, dateFormatter)
        val to = raw.getDate(StockListItemPredeterminationKeys.TO_DATE, dateFormatter)
        val range = from .. to

        return StockListItemPredetermination(
            id = raw.getString(StockListItemPredeterminationKeys.ID),
            placeId = raw.getString(StockListItemPredeterminationKeys.PLACE_ID),
            item = item,
            period = range
        )
    }

    fun getStockList(place: SpacePOIPlace): StockList {
        val cashed = stocks[place.id]
        if (cashed != null) {
            return cashed
        }

        val sheet = getSheetNames(service, shopsSpreadsheetId)
            .firstOrNull { it == place.id }
        if (sheet != null) {
            val stock = downloadStock(sheet)
            stocks[place.id] = stock
            return stock
        }

        getDescriptors()
        val predeterminations = getPredeterminations()
        val newStock = generateStockList(place, predeterminations)
        uploadStock(place, newStock)
        return newStock
    }

    fun updateStockItemAmount(place: SpacePOIPlace, item: StockListItem, newAmount: Int) {
        val stock = stocks[place.id] ?: return
        val index = stock.indexOf(item)
        val range = "${place.id}!${stockAmountColumn}${index+1}"
        uploadCell(shopsSpreadsheetId, range, newAmount.toString()) { success ->
            if (success) item.amount = newAmount
        }
    }

    private fun uploadStock(place: SpacePOIPlace, stock: StockList) {
        if (place.id !in getSheetNames(service, shopsSpreadsheetId)) {
            addSheet(shopsSpreadsheetId, place.id)
        }

        val raw = stock.map { listOf(
            it.descriptor.id,
            it.amount.toString(),
            it.price.toString(),
            it.isPreOrder.toString()
        ) }

        uploadData(
            spreadsheetId = shopsSpreadsheetId,
            sheet = place.id,
            column = 1,
            row = 1,
            data = raw
        ) { success ->
            if (success) stocks[place.id] = stock
        }
    }

    private fun downloadStock(list: String): StockList {
        val result = mutableListOf<StockListItem>()

        val response = service
            .spreadsheets()
            .values()
            .get(shopsSpreadsheetId, list + stockListRange)
            .execute()
        val rawStock = response.getValues()?.map { it.toTypedArray() }?.toTypedArray()

        try {
            if (rawStock != null) {
                for (rawStockItem in rawStock) {
                    val id = rawStockItem.getString(StockListItemKeys.ID)
                    val descriptor = descriptors.first { it.id == id }
                    val item = StockListItem(
                        descriptor = descriptor,
                        amount = rawStockItem.getInt(StockListItemKeys.AMOUNT),
                        price = rawStockItem.getInt(StockListItemKeys.PRICE),
                        isPreOrder = rawStockItem.getBoolean(StockListItemKeys.IS_PREORDER, false)
                    )

                    result.add(item)
                }
            }
        } catch (e: Exception) {
            Log.e(logTag, "Stock list data invalid: ${e.toString()}")
        }

        return result.toTypedArray()
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
    val rawCategories = raw.getSplitedString(ItemDescriptorKeys.LOOT_CATEGORIES, ",")
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

    val rawCategories = raw.getSplitedString(ItemDescriptorKeys.SHOP_CATEGORIES, ",")
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