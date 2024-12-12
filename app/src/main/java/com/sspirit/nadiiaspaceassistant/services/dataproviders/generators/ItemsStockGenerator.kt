package com.sspirit.nadiiaspaceassistant.services.dataproviders.generators

import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StockListItem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import kotlin.random.Random

fun generateStockList(place: SpacePOIPlace) : StockList {
    val stockList = mutableListOf<StockListItem>()
    val available = ItemDataProvider.descriptors.filter { match(it, place) }

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

    return stockList.toTypedArray()
}

private fun match(descriptor: ItemDescriptor, place: SpacePOIPlace): Boolean {
    val buying = descriptor.buying ?: return false
    return buying.storesTypes.any { it.placeType == place.type }
}