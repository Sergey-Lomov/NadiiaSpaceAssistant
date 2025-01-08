package com.sspirit.nadiiaspaceassistant.screens.items

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.viewmodels.ItemSelectorViewModel

@Composable
fun <T>ItemSelectorView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<ItemSelectorViewModel<T>>(modelId) ?: return
    val descriptors = ItemDataProvider.descriptors
        .filter { it !in model.unavailable }
    val grouper = model.grouper
    val groups = grouper?.let { descriptors.groupBy { grouper(it) } }

    ScreenWrapper(navigator, "Выбор предмета") {
        ScrollableColumn {
            if (groups != null)
                GroupedList(groups, model, navigator)
            else
                ItemsList(descriptors, model, navigator)
        }
    }
}

@Composable
private fun <T> GroupedList(
    groups: Map<T, List<ItemDescriptor>>,
    model: ItemSelectorViewModel<T>,
    navigator: NavHostController
) {
    if (model.order == null) return
    for (groupKey in model.order) {
        val items = groups[groupKey] ?: continue
        HeaderText(groupKey.toString())
        Spacer(Modifier.height(16.dp))
        ItemsList(items, model, navigator)
        if (groupKey !== model.order.last())
            SpacedHorizontalDivider()
    }
}

@Composable
private fun <T> ItemsList(
    items: List<ItemDescriptor>,
    model: ItemSelectorViewModel<T>,
    navigator: NavHostController
) {
    for (item in items) {
        CenteredInfoTextCard(item.title) {
            model.onSelect(item)
            navigator.popBackStack()
        }
        if (item != items.last())
            Spacer(Modifier.height(8.dp))
    }
}