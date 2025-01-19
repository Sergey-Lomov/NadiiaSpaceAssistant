package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockItem
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredeterminationType
import com.sspirit.nadiiaspaceassistant.models.items.StoreInventory
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.StockItemPredeterminationTableRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.StockItemTableRow
import com.sspirit.nadiiaspaceassistant.services.fabrics.generateStockList
import com.sspirit.nadiiaspaceassistant.services.fabrics.preorder
import java.time.LocalDate

private const val spreadsheetId = "1wlzHqYjTI68koZbLcIRRtOayXpeuMYxrRhtrjwsMh8s"
private const val predeterminationsFirstRow = 3
private const val predeterminationsSheet = "Orders"
private const val predeterminationsRange = "$predeterminationsSheet!A$predeterminationsFirstRow:H100"
private const val stockListRange = "!A1:E50"

object StoresDataProvider : GoogleSheetDataProvider() {
    private var inventories = mutableMapOf<String, StoreInventory>()

    fun get(place: SpacePOIPlace): StoreInventory? {
        return inventories[place.id]
    }

    fun prepareStock(place: SpacePOIPlace) {
        ItemDataProvider.downloadDescriptors()

        val cashed = inventories[place.id]
        if (cashed != null) return

        val predeterminations = downloadPredeterminations()
        val stockSheet = getSheetNames(spreadsheetId)
            .firstOrNull { it == place.id }
        val stock = if (stockSheet != null)
            downloadStock(stockSheet)
        else
            generateStockList(place, predeterminations)

        if (stockSheet == null) uploadStock(place, stock)

        val available = ItemDataProvider.descriptorsFor(place)
            .filter { !it.isLocked }
        val preorders = predeterminations
            .filter { it.type == StockItemPredeterminationType.PREORDER }
            .filter { it.placeId == place.id }

        inventories[place.id] = StoreInventory(
            stock = stock,
            available = available.toMutableList(),
            preorders = preorders.toMutableList()
        )
    }

    fun updateStockItemAmount(place: SpacePOIPlace, item: StockItem, newAmount: Int) {
        val row = firstRowWithText(item.id, spreadsheetId, place.id) ?: return
        val oldAmount = item.amount
        item.amount = newAmount
        val data = StockItemTableRow.from(item).toRawData()
        uploadRow(spreadsheetId, place.id, row, data) { success ->
            if (!success) item.amount = oldAmount
        }
    }

    fun addPredetermination(order: StockItemPredetermination, completion: Completion = null) {
        val row = StockItemPredeterminationTableRow.from(order)
        val data = listOf(row.toRawData())
        insert(spreadsheetId, predeterminationsSheet, predeterminationsFirstRow, data) { success ->
            if (success)
                handleNewPredetermination(order)
            completion?.invoke(success)
        }
    }

    fun removePredetermination(order: StockItemPredetermination, completion: Completion = null) {
        val row = firstRowWithText(order.id, spreadsheetId, predeterminationsSheet) ?: return
        deleteRow(spreadsheetId, predeterminationsSheet, row) { success ->
            if (success) {
                inventories.values.forEach {
                    it.preorders.remove(order)
                }
            }
            completion?.invoke(success)
        }
    }

    private fun handleNewPredetermination(predetermination: StockItemPredetermination) {
        val inventory = inventories[predetermination.placeId] ?: return
        if (predetermination.type == StockItemPredeterminationType.PREORDER) {
            inventory.preorders.add(predetermination)
        }
    }

    private fun uploadStock(place: SpacePOIPlace, stock: StockList) {
        if (place.id !in getSheetNames(spreadsheetId)) {
            addSheet(spreadsheetId, place.id)
        }

        val data = stock.map { StockItemTableRow.from(it).toRawData() }
        uploadData(
            spreadsheetId = spreadsheetId,
            sheet = place.id,
            column = 1,
            startRow = 1,
            data = data
        )
    }

    private fun downloadStock(sheet: String): StockList {
        val response = request(spreadsheetId, sheet + stockListRange)
        val rows = parseToArray(response, "Stock list $sheet data invalid", StockItemTableRow::parse)
        return rows.mapNotNull { it.toStockItem() }.toTypedArray()
    }

    private fun downloadPredeterminations(): Array<StockItemPredetermination> {
        val response = request(spreadsheetId, predeterminationsRange)
        val rows = parseToArray(response, "Stock item predetermination data invalid", StockItemPredeterminationTableRow::parse)
        return rows.mapNotNull { it.toStockItemPredetermination() }.toTypedArray()
    }
}