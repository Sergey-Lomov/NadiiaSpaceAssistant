package com.sspirit.nadiiaspaceassistant.screens.cosmology.places

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.items.StockListItem
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ShopsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun SpacePOIStoreDetailsView(place: SpacePOIPlace, isLoading: MutableState<Boolean>) {
    val stock = ShopsDataProvider.get(place)
    if (stock == null) {
        CoroutineLaunchedEffect(loadingState = isLoading) {
            ShopsDataProvider.prepareStock(place)
        }
    } else {
        ScrollableColumn {
            ElementsList(stock) { StockItemCard(it, place) }
        }
    }
}

@Composable
fun StockItemCard(item: StockListItem, place: SpacePOIPlace) {
    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Text(
                text = item.descriptor.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .wrapContentHeight(align = CenterVertically),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            ControlPanel(item, place)
        }
    }
}

@Composable
private fun ControlPanel(item: StockListItem, place: SpacePOIPlace) {
    val amount = remember { mutableIntStateOf(item.amount) }
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        val priceColor = if (item.isPreOrder) colorResource(id = R.color.soft_yellow) else Color.Black
        Text(
            text = item.price.toString() + "$",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = priceColor,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(align = CenterVertically),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        AmountChanger(
            title = "+",
            item = item,
            place = place,
            newAmount = item.amount + 1,
            amountState = amount
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = amount.intValue.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(align = CenterVertically),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.width(8.dp))

        AmountChanger(
            title = "-",
            item = item,
            place = place,
            newAmount = item.amount - 1,
            amountState = amount
        )

    }
}

@Composable
private fun AmountChanger(
    title: String,
    item: StockListItem,
    place: SpacePOIPlace,
    newAmount: Int,
    amountState: MutableIntState
) {
    CoroutineButton(
        title = title,
        routine = {
            ShopsDataProvider.updateStockItemAmount(
                place = place,
                item = item,
                newAmount = newAmount
            )
        },
        completion = {
            amountState.intValue = item.amount
        }
    )
}