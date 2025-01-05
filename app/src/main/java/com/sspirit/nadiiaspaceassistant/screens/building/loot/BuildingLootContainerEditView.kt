package com.sspirit.nadiiaspaceassistant.screens.building.loot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainerItem
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.building.ItemSelectorViewModel
import kotlin.math.max

@Composable
fun BuildingLootContainerEditView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingLootContainerViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }
    val updater = remember { mutableIntStateOf(0) }
    val container = model.element.copy()

    LaunchedEffect(updater.intValue) {  }

    ScreenWrapper(navigator, "Лут-контейнер", isLoading) {
        ScrollableColumn {
            HeaderText(container.group.title)
            Spacer(Modifier.height(8.dp))
            CenteredRegularText(container.group.description)

            SpacedHorizontalDivider()
            for (lootItem in container.items) {
                LootItemCard(container, lootItem, updater)
                if (lootItem !== container.items.last())
                    Spacer(Modifier.height(8.dp))
            }

            SpacedHorizontalDivider()
            AddItemButton(container, navigator)
            Spacer(Modifier.height(8.dp))
            SaveButton(model, container, navigator)
        }
    }
}

@Composable
private fun LootItemCard(
    container: BuildingLootContainer,
    lootItem: BuildingLootContainerItem,
    updater: MutableIntState
) {
    val complexItems = container.room.location.sector.building.availableLoot
        .flatMap {  it.items }
        .map { it.descriptor }
    val groupItems = container.group.items
        .map { it.descriptor }
    val containerUnrecomended = lootItem.item !in groupItems
    val complexUnrecomended = lootItem.item !in complexItems

    Card {
        Column(Modifier.padding(16.dp)) {
            CenteredRegularText(lootItem.item.title)
            Spacer(Modifier.height(8.dp))

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

            TitleValueRow("Количесво", value = lootItem.amount)

            Spacer(Modifier.height(8.dp))
            Row {
                StyledButton("+") {
                    lootItem.amount += 1
                    updater.intValue += 1
                }
                Spacer(Modifier.width(8.dp))
                StyledButton("-") {
                    lootItem.amount = max(0, lootItem.amount - 1)
                    updater.intValue += 1
                }
                Spacer(Modifier.width(8.dp))
                AutosizeStyledButton("Удалить") {
                    container.items.remove(lootItem)
                    updater.intValue += 1
                }
            }
        }
    }
}

@Composable
private fun AddItemButton(container: BuildingLootContainer, navigator: NavHostController) {
    AutosizeStyledButton("Добавить предмет") {
        val model = ItemSelectorViewModel(container) { item ->
            val lootItem = BuildingLootContainerItem(item, 1)
            container.items.add(lootItem)
        }
        navigator.navigateWithModel(Routes.ItemsSelector, model)
    }
}

@Composable
private fun SaveButton(model: BuildingLootContainerViewModel, container: BuildingLootContainer, navigator: NavHostController) {
    val state = LocalSWLoadingState.current ?: return
    AutosizeStyledButton("Сохранить") {
        simpleCoroutineLaunch(state) {
            DataProvider.replaceLootContainer(model.missionId, container) {
                mainLaunch {
                    navigator.popBackStack()
                    }
            }
        }
    }
}