package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalWall = compositionLocalOf<BuildingWall?> { null }
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
                LocalMissionId provides model.missionId,
                LocalWall provides wall
            ) {
                ScrollableColumn {
                    BuildingWallCard(wall)
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
private fun MakeHoleButton() {
    val missionId = LocalMissionId.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val wall = LocalWall.current ?: return
    val isDestructible = wall.material.isDestructible

    AutosizeStyledButton(
        title = "Пробить дыру",
        enabled = isDestructible
    ) {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateWallHole(missionId, wall, true)
        }
    }
}

@Composable
private fun RemoveHoleButton() {
    val missionId = LocalMissionId.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val wall = LocalWall.current ?: return

    AutosizeStyledButton("Убрать дыру") {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateWallHole(missionId, wall, false)
        }
    }
}