package com.sspirit.nadiiaspaceassistant.screens.building.loot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.items.QuantumStorage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLootContainerCard
import com.sspirit.nadiiaspaceassistant.screens.items.ui.QuantumStorageCard
import com.sspirit.nadiiaspaceassistant.screens.items.ui.QuantumStorageTool
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.QuantumStorageDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.QuantumStorageIdEditViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.QuantumStoragesViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

typealias BuildingLootContainerViewModel = BuildingElementViewModel<BuildingLootContainer>

@Composable
fun BuildingLootContainerView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingLootContainerViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Лут-контейнер", isLoading) {
        ScrollableColumn(Modifier.padding(16.dp)) {
            CenteredRegularText(model.element.group.description)
            Spacer(Modifier.height(16.dp))
            BuildingLootContainerCard(model.element, true)
            SpacedHorizontalDivider()
            GrabButton(model, navigator)
            SpacedHorizontalDivider()
            ArchivesDetailsButton(model, navigator)
            Spacer(Modifier.height(8.dp))
            ArchiveButton(model, navigator)
            Spacer(Modifier.height(8.dp))
            EditButton(model, navigator)
        }
    }
}

@Composable
private fun GrabButton(model: BuildingLootContainerViewModel, navigator: NavHostController) {
    val state = LocalSWLoadingState.current ?: return

    AutosizeStyledButton("Забрать") {
        simpleCoroutineLaunch(state) {
            DataProvider.removeLootContainer(model.missionId, model.element) {
                if (it) mainLaunch {
                    navigator.popBackStack()
                }
            }
        }
    }
}

@Composable
private fun ArchiveButton(model: BuildingLootContainerViewModel, navigator: NavHostController) {
    val state = LocalSWLoadingState.current ?: return

    AutosizeStyledButton(
        title = "Квант. архивировать",
        enabled = model.element.nodes.isNotEmpty()
    ) {
        val idModel = QuantumStorageIdEditViewModel { id ->
            simpleCoroutineLaunch(state) {
                val storage = QuantumStorage(id, model.element.nodes.toMutableList())
                QuantumStorageDataProvider.add(storage) { success ->
                    if (!success) return@add
                    model.element.nodes = arrayOf()
                    model.element.quantumStorages = model.element.quantumStorages.plus(storage)
                    DataProvider.replaceLootContainer(model.missionId, model.element) {
                        mainLaunch {
                            navigator.popBackStack()
                        }
                    }
                }
            }
        }
        navigator.navigateWithModel(Routes.ItemsQuantumStorageIdEdit, idModel)
    }
}

@Composable
private fun EditButton(model: BuildingLootContainerViewModel, navigator: NavHostController) {
    AutosizeStyledButton("Редактировать") {
        navigator.navigateWithModel(BuildingRoutes.LootContainerEdit, model)
    }
}

@Composable
private fun ArchivesDetailsButton(model: BuildingLootContainerViewModel, navigator: NavHostController) {
    AutosizeStyledButton("Детали архивов") {
        val tools = arrayOf(QuantumStorageTool.DELETE, QuantumStorageTool.EDIT)
        val viewModel = QuantumStoragesViewModel(tools) {
            model.element.quantumStorages.asIterable()
        }
        navigator.navigateWithModel(Routes.ItemsQuantumStorages, viewModel)
    }
}