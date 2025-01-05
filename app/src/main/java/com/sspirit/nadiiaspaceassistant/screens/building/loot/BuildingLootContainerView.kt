package com.sspirit.nadiiaspaceassistant.screens.building.loot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLootContainerCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

typealias BuildingLootContainerViewModel = BuildingElementViewModel<BuildingLootContainer>

@Composable
fun BuildingLootContainerView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingLootContainerViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Лут-контейнер", isLoading) {
        Column(Modifier.padding(16.dp)) {
            BuildingLootContainerCard(model.element)
            Spacer(Modifier.weight(1f))
            GrabButton(model, navigator)
            SpacedHorizontalDivider()
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
private fun EditButton(model: BuildingLootContainerViewModel, navigator: NavHostController) {
    AutosizeStyledButton("Редактировать") {
        navigator.navigateWithModel(Routes.BuildingLootContainerEdit, model)
    }
}