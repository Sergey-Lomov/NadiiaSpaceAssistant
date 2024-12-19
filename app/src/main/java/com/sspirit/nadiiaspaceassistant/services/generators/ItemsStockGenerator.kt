package com.sspirit.nadiiaspaceassistant.services.generators

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockListItem
import com.sspirit.nadiiaspaceassistant.models.items.StockListItemPredetermination
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import java.time.LocalDate
import kotlin.random.Random

fun generateStockList(place: SpacePOIPlace, predeterminations: Array<StockListItemPredetermination>) : StockList {
    val stockList = mutableListOf<StockListItem>()
    val orders = predeterminations
        .filter { it.placeId == place.id && it.period.contains(LocalDate.now()) }
        .map { it.item }
    val ordersIds = orders.map { it.descriptor.id }
    val available = ItemDataProvider.descriptors
        .filter { match(it, place) && it.id !in ordersIds}

    for (descriptor in available) {
        val buying = descriptor.buying ?: continue
        if (buying.chance < Random.nextFloat()) continue
        val amount = buying.amount.random()
        val price = buying.price.random()

        val item = StockListItem(
            descriptor = descriptor,
            amount = amount,
            price = price,
            isPreOrder = false
        )

        stockList.add(item)
    }
    
    stockList.addAll(orders)

    return stockList.toTypedArray()
}

private fun match(descriptor: ItemDescriptor, place: SpacePOIPlace): Boolean {
    val buying = descriptor.buying ?: return false
    return buying.storesTypes.any { it.placeType == place.type }
}