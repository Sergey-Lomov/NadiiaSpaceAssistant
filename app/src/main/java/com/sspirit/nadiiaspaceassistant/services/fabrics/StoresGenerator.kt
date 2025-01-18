package com.sspirit.nadiiaspaceassistant.services.fabrics

import android.content.ClipData.Item
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockItem
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredeterminationType
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.utils.plusDays
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

fun generateStockList(
    place: SpacePOIPlace,
    predeterminations: Array<StockItemPredetermination>
): StockList {
    val stock = mutableListOf<StockItem>()
    val searchResults = predeterminations
        .filter { it.placeId == place.id }
        .filter { it.period.contains(LocalDate.now()) }
        .filter { it.type == StockItemPredeterminationType.SEARCH_RESULT }
        .map { it.item }
    val searchIds = searchResults.map { it.descriptor.id }
    val available = ItemDataProvider.descriptorsFor(place)
        .filter { it.id !in searchIds}
        .filter { !it.isLocked }

    for (descriptor in available) {
        val buying = descriptor.buying ?: continue
        if (buying.chance < Random.nextFloat()) continue
        val amount = buying.amount.random()
        val price = buying.price.random()
        val item = StockItem(descriptor, amount, price)
        stock.add(item)
    }

    stock.addAll(searchResults)

    return stock.toTypedArray()
}

private val deliveryRange = 2..3
private val waitingRange = 2..4

fun preorder(descriptor: ItemDescriptor, amount: Int, place: SpacePOIPlace): StockItemPredetermination {
    val item = StockItem(descriptor, amount, 0)
    val from = LocalDate.now().plusDays(deliveryRange.random())
    val to = from.plusDays(waitingRange.random())
    return StockItemPredetermination(
        id = UUID.randomUUID().toString(),
        type = StockItemPredeterminationType.PREORDER,
        placeId = place.id,
        item = item,
        period = from..to
    )
}