package com.sspirit.nadiiaspaceassistant.screens.building.loot

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.ItemsStorageNode
import com.sspirit.nadiiaspaceassistant.models.items.ItemDescriptor
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.items.ItemsStorageNodesEditView
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

enum class LootContainerItemsResolutions(val title: String) {
    CONTAINER_RECOMMENDED("Рекомендовано для контейнера"),
    COMPLEX_RECOMMENDED("Рекомендовано для комплекса"),
    UN_RECOMMENDED("Не рекомендовано");

    override fun toString(): String = title
}

private val order = arrayOf(
    LootContainerItemsResolutions.CONTAINER_RECOMMENDED,
    LootContainerItemsResolutions.COMPLEX_RECOMMENDED,
    LootContainerItemsResolutions.UN_RECOMMENDED,
)

@Composable
fun BuildingLootContainerEditView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingLootContainerViewModel>(modelId) ?: return

    ItemsStorageNodesEditView(
        header = model.element.group.title,
        info = model.element.group.description,
        sourceNodes = model.element.nodes,
        navigator = navigator,
        selectorRoute = Routes.LootContainerItemsSelector,
        itemsGrouper = { item -> itemResolution(item, model.element) },
        itemsGroupsOrder = order,
        nodeStatusBuilder = { node -> StatusView(model.element, node) },
        onSave = { nodes, state ->
            model.element.nodes = nodes
            simpleCoroutineLaunch(state) {
                DataProvider.replaceLootContainer(model.missionId, model.element) {
                    mainLaunch {
                        navigator.popBackStack()
                    }
                }
            }
        }
    )
}

private fun itemResolution(item: ItemDescriptor, container: BuildingLootContainer): LootContainerItemsResolutions {
    val complexItems = container.room.location.sector.building.availableLoot
        .flatMap {  it.items }
        .map { it.descriptor }
    val groupItems = container.group.items
        .map { it.descriptor }
    val containerRecommended = item in groupItems
    val complexRecommended = item in complexItems

    return if (containerRecommended) LootContainerItemsResolutions.CONTAINER_RECOMMENDED
    else if (complexRecommended) LootContainerItemsResolutions.COMPLEX_RECOMMENDED
    else LootContainerItemsResolutions.UN_RECOMMENDED
}

@Composable
private fun StatusView(container: BuildingLootContainer, node: ItemsStorageNode) {
    val complexItems = container.room.location.sector.building.availableLoot
        .flatMap {  it.items }
        .map { it.descriptor }
    val groupItems = container.group.items
        .map { it.descriptor }
    val containerUnrecomended = node.item !in groupItems
    val complexUnrecomended = node.item !in complexItems

    if (complexUnrecomended)
        CenteredRegularText(
            text = "Нерекомендовано для объекта",
            color = colorResource(R.color.soft_red)
        )
    else if (containerUnrecomended)
        CenteredRegularText(
            text = "Нерекомендовано для контейнера",
            color = colorResource(R.color.soft_yellow)
        )
}