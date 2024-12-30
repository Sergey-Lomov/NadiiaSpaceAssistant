package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToCeiling
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToFloor
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

private val LocalModel = compositionLocalOf<BuildingWallViewModel?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

typealias BuildingWallViewModel = RelativeBuildingElementViewModel<BuildingWall>

@Composable
fun BuildingWallView(modelId: String, navController: NavHostController) {
    val isLoading = remember { mutableStateOf(false) }
    val model = ViewModelsRegister.get<BuildingWallViewModel>(modelId) ?: return
    val wall = model.element

    ScreenWrapper(navController, "Стена") {
        if (isLoading.value)
            LoadingIndicator()
        else {
            CompositionLocalProvider(
                LocalLoadingState provides isLoading,
                LocalModel provides model,
                LocalNavigator provides navController
            ) {
                ScrollableColumn {
                    BuildingWallCard(wall)
                    ConnectedRooms()
                    SpacedHorizontalDivider()
                    if (wall.hasHole) {
                        RemoveHoleButton()
                    } else {
                        MakeHoleButton()
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectedRooms() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val rooms = model.element.rooms
        .filter { it != model.viewPoint }
        .filter { it.isValid }

    if (rooms.isNotEmpty()) {
        SpacedHorizontalDivider()
        for (room in rooms) {
            BuildingRoomOverviewCard(room, true) {
                navigator.navigateToRoom(model.missionId, room)
            }
        }
    } else
        HeaderText("За стеной пусто")
}

@Composable
private fun MakeHoleButton() {
    val model = LocalModel.current ?: return
    val wall = model.element
    val loadingState = LocalLoadingState.current ?: return
    val isDestructible = wall.material.isDestructible

    AutosizeStyledButton(
        title = "Пробить дыру",
        enabled = isDestructible && !wall.hasHole
    ) {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateWallHole(model.missionId, wall, true)
        }
    }
}

@Composable
private fun RemoveHoleButton() {
    val model = LocalModel.current ?: return
    val wall = model.element
    val loadingState = LocalLoadingState.current ?: return

    AutosizeStyledButton(
        title = "Убрать дыру",
        enabled = wall.hasHole
    ) {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateWallHole(model.missionId, wall, false)
        }
    }
}