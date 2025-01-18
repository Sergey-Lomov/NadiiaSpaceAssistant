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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlace
import com.sspirit.nadiiaspaceassistant.models.cosmology.SpacePOIPlaceType
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.items.StockItem
import com.sspirit.nadiiaspaceassistant.models.items.StockItemPredetermination
import com.sspirit.nadiiaspaceassistant.models.items.StockList
import com.sspirit.nadiiaspaceassistant.models.items.StoreInventory
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.StoresDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.preorder
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.CoroutineButton
import com.sspirit.nadiiaspaceassistant.ui.CoroutineLaunchedEffect
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.LocalSWUpdater
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import com.sspirit.nadiiaspaceassistant.utils.daysToNow
import com.sspirit.nadiiaspaceassistant.utils.format
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.update
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import org.checkerframework.checker.units.qual.C
import java.time.LocalDate

@Composable
fun SpacePOIStoreDetailsView(
    place: SpacePOIPlace,
    isLoading: MutableState<Boolean>,
    navigator: NavHostController
) {
    val inventory = StoresDataProvider.get(place)
    if (inventory == null) {
        CoroutineLaunchedEffect(loadingState = isLoading) {
            StoresDataProvider.prepareStock(place)
        }
    } else {
        ScrollableColumn {
            Section("Предзаказы", inventory.preorders) { PreorderCard(it) }
            Section("В продаже", inventory.stock.toList()) { StockItemCard(it, place) }
            Section("Можно заказать", inventory.available) { AvailableCard(it, place, navigator) }
        }
    }
}

@Composable
fun <T> Section(
    title: String,
    elements: List<T>,
    cardBuilder: @Composable (T) -> Unit
) {
    if (elements.isEmpty()) return
    SpacedHorizontalDivider()
    HeaderText(title)
    Spacer(Modifier.height(8.dp))
    ElementsList(elements) { cardBuilder(it) }
}

@Composable
fun PreorderCard(preorder: StockItemPredetermination) {
    val from = preorder.period.start
    val to = preorder.period.endInclusive
    val daysToDelivery = from.daysToNow()
    val daysToCancel = to.daysToNow()

    val deliveryDetails = if (daysToDelivery > 0) "$daysToDelivery д." else "уже"
    val deliveryString = "$deliveryDetails (${from.format()})"

    val cancelDetails = if (daysToDelivery > 0  && daysToCancel > 0)
        "${daysToCancel- daysToDelivery} д."
    else if (daysToDelivery < 0  && daysToCancel > 0)
        "еще ${daysToCancel- daysToDelivery} д."
    else
        "истекло"
    val cancelString = "$cancelDetails (${to.format()})"

    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            HeaderText(preorder.item.descriptor.title)
            Spacer(Modifier.height(8.dp))
            PreorderStatus(preorder)
            Spacer(Modifier.height(8.dp))
            TitlesValuesList(
                "Кол-во" to preorder.item.amount,
                "Доставка" to deliveryString,
                "Хранение" to cancelString
            )
            ReceivePreorderButton(preorder)
        }
    }
}

@Composable
fun ReceivePreorderButton(preorder: StockItemPredetermination) {
    val loadingState = LocalSWLoadingState.current ?: return

    if (LocalDate.now() in preorder.period) {
        Spacer(Modifier.height(8.dp))
        AutosizeStyledButton("Получить") {
            simpleCoroutineLaunch (loadingState) {
                StoresDataProvider.removePredetermination(preorder)
            }
        }
    }
}

@Composable
fun PreorderStatus(preorder: StockItemPredetermination) {
    when {
        LocalDate.now() in preorder.period ->
            CenteredRegularText(
                text = "Можно получить",
                color = colorResource(R.color.soft_green),
                weight =  FontWeight.Bold
            )

        preorder.period.start.isAfter(LocalDate.now()) ->
            CenteredRegularText(
                text = "В пути",
                color = colorResource(R.color.soft_yellow),
                weight =  FontWeight.Bold
            )

        preorder.period.endInclusive.isBefore(LocalDate.now()) ->
            CenteredRegularText(
                text = "Просрочено",
                color = colorResource(R.color.soft_red),
                weight =  FontWeight.Bold
            )
    }
}

@Composable
fun AvailableCard(descriptor: ItemDescriptor, place: SpacePOIPlace, navigator: NavHostController) {
    val loadingState = LocalSWLoadingState.current ?: return
    val price = descriptor.buying?.price?.average() ?: return

    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            HeaderText(descriptor.title)
            Spacer(Modifier.height(8.dp))
            AutosizeStyledButton("Предзаказать") {
                val range = 1..5
                val info = "Выберите кол-во товара и заплатите аванс: 100% средней стоиомсти\n"
                val optionsStrings = range.map { "$it шт. за ${(it * price).toInt()}" }
                val model = InfoDialogViewModel(
                    title = "Оформление предзаказа",
                    info = info + stringsToList(optionsStrings)
                )

                range.forEach { amount ->
                    model.actions["Заказать $amount шт."] = {
                        val preorder = preorder(descriptor, amount, place)
                        navigator.popBackStack()
                        simpleCoroutineLaunch (loadingState) {
                            StoresDataProvider.addPredetermination(preorder)
                        }
                    }
                }

                navigator.navigateWithModel(Routes.InfoDialog, model)
            }
        }
    }
}

@Composable
fun StockItemCard(item: StockItem, place: SpacePOIPlace) {
    Card {
        Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            HeaderText(item.descriptor.title)
            Spacer(Modifier.height(8.dp))
            ReputationLabel(item.descriptor, place)
            StockItemControlPanel(item, place)
        }
    }
}

@Composable
fun ReputationLabel(descriptor: ItemDescriptor, place: SpacePOIPlace) {
    val requirement = descriptor.buying?.reputationRequirement ?: return
    if (requirement == 0) return
    val ownerTitle = when (place.type) {
        SpacePOIPlaceType.JPK_STORE -> "JPK Inc."
        SpacePOIPlaceType.XENOPHARM_STORE -> "XenoPharm"
        SpacePOIPlaceType.VERSEMINING_STORE -> "Verse Mining"
        SpacePOIPlaceType.PICKAXE_STORE -> "Братством Кирки"
        else -> ""
    }
    CenteredRegularText("Требуется репутация $requirement c $ownerTitle")
}

@Composable
private fun StockItemControlPanel(item: StockItem, place: SpacePOIPlace) {
    val amount = remember { mutableIntStateOf(item.amount) }
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = item.price.toString() + "$",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
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
    item: StockItem,
    place: SpacePOIPlace,
    newAmount: Int,
    amountState: MutableIntState
) {
    CoroutineButton(
        title = title,
        routine = {
            StoresDataProvider.updateStockItemAmount(
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