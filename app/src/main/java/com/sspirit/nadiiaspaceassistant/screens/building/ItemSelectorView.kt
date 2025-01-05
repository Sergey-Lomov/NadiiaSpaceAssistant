package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.ItemDataProvider
import com.sspirit.nadiiaspaceassistant.ui.CenteredInfoTextCard
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.viewmodels.building.ItemSelectorViewModel

private enum class ItemsResolutions(val title: String) {
    NO_RESOLUTION("Полный список"),
    CONTAINER_RECOMMENDED("Рекомендовано для контейнера"),
    COMPLEX_RECOMMENDED("Рекомендовано для комплекса"),
    UN_RECOMMENDED("Не рекомендовано"),
}

private val order = arrayOf(
    ItemsResolutions.CONTAINER_RECOMMENDED,
    ItemsResolutions.COMPLEX_RECOMMENDED,
    ItemsResolutions.UN_RECOMMENDED,
    ItemsResolutions.NO_RESOLUTION
)

@Composable
fun ItemSelectorView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<ItemSelectorViewModel>(modelId) ?: return
    val container = model.container
    val unavailable = container?.items?.map { it.item } ?: listOf()
    val descriptors = ItemDataProvider.descriptors
        .filter { it !in unavailable }
    val groups = descriptors.groupBy { itemResolution(it, container) }

    ScreenWrapper(navigator, "Выбор предмета") {
        ScrollableColumn {
            for (resolution in order) {
                val items = groups[resolution] ?: continue
                HeaderText(resolution.title)
                Spacer(Modifier.height(16.dp))
                for (item in items) {
                    CenteredInfoTextCard(item.title) {
                        model.onSelect(item)
                        navigator.popBackStack()
                    }

                    if (item != items.last())
                        Spacer(Modifier.height(8.dp))
                }

                if (resolution !== order.last())
                    SpacedHorizontalDivider()
            }
        }
    }
}

private fun itemResolution(item: ItemDescriptor, container: BuildingLootContainer?): ItemsResolutions {
    if (container == null) return ItemsResolutions.NO_RESOLUTION

    val complexItems = container.room.location.sector.building.availableLoot
        .flatMap {  it.items }
        .map { it.descriptor }
    val groupItems = container.group.items
        .map { it.descriptor }
    val containerRecomended = item in groupItems
    val complexRecomended = item in complexItems

    return if (containerRecomended) ItemsResolutions.CONTAINER_RECOMMENDED
    else if (complexRecomended) ItemsResolutions.COMPLEX_RECOMMENDED
    else ItemsResolutions.UN_RECOMMENDED
}