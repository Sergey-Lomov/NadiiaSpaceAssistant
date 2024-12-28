package com.sspirit.nadiiaspaceassistant.services.dataproviders

import android.util.Log
import com.sspirit.nadiiaspaceassistant.utils.getBoolean
import com.sspirit.nadiiaspaceassistant.utils.getDate
import com.sspirit.nadiiaspaceassistant.utils.getInt
import com.sspirit.nadiiaspaceassistant.utils.getString
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockListItem
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemKeys
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemPredeterminationKeys
import com.sspirit.nadiiaspaceassistant.services.generators.generateStockList

private const val shopsSpreadsheetId = "1wlzHqYjTI68koZbLcIRRtOayXpeuMYxrRhtrjwsMh8s"
private const val predeterminationsRange = "Orders!A1:H100"
private const val stockListRange = "!A1:E50"
private const val stockAmountColumn = "B"

object ShopsDataProvider : GoogleSheetDataProvider() {
    private var stocks = mutableMapOf<String, StockList>()

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
        val descriptor = ItemDataProvider.descriptors.first { it.id == id }
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

        ItemDataProvider.getDescriptors()
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
            startRow = 1,
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
                    val descriptor = ItemDataProvider.descriptors.first { it.id == id }
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