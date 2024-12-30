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
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSlabCard
import com.sspirit.nadiiaspaceassistant.services.ClosuresManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToCeiling
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToFloor
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalSlab = compositionLocalOf<BuildingSlab?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalViewpointLevel = compositionLocalOf<Int?> { null }

typealias BuildingSlabViewModel = RelativeBuildingElementViewModel<BuildingSlab>

@Composable
fun BuildingSlabView(modelId: String, navController: NavHostController) {
    val model = ViewModelsRegister.get<BuildingSlabViewModel>(modelId) ?: return
    val viewPointLevel = model.viewPoint?.location?.level
    val isLoading = remember { mutableStateOf(false) }

    val header = when (model.element.level) {
        viewPointLevel?.locationLevelToFloor() -> "Пол"
        viewPointLevel?.locationLevelToCeiling() -> "Потолок"
        else -> "Перекрытие"
    }

    ScreenWrapper(navController, header) {
        if (isLoading.value)
            LoadingIndicator()
        else {
            CompositionLocalProvider(
                LocalLoadingState provides isLoading,
                LocalMissionId provides model.missionId,
                LocalSlab provides model.element,
                LocalViewpointLevel provides viewPointLevel,
                LocalNavigator provides navController
            ) {
                ScrollableColumn {
                    BuildingSlabCard(model.element)
                    Spacer(Modifier.height(8.dp))
                    ConnectedRooms()
                    SpacedHorizontalDivider()
                    MakeHoleButton()
                    Spacer(Modifier.height(8.dp))
                    JumpButton()
//                    Spacer(Modifier.height(8.dp))
//                    CarefullyDownButton()
//                    Spacer(Modifier.height(8.dp))
//                    DownByHeapButton()
//                    Spacer(Modifier.height(8.dp))
//                    UpByHeapButton()
                    SpacedHorizontalDivider()
                    RemoveHoleButton()
                }
            }
        }
    }
}

@Composable
private fun ConnectedRooms() {
    val slab = LocalSlab.current ?: return
    val missionId = LocalMissionId.current ?: return
    val navigator = LocalNavigator.current ?: return
    val viewPoint = LocalViewpointLevel.current
    val isFloor = viewPoint?.locationLevelToFloor() == slab.level
    val isCeiling = viewPoint?.locationLevelToCeiling() == slab.level

    SpacedHorizontalDivider()
    if (!isFloor) {
        val upRoom = slab.upRoom
        if (upRoom != null) {
            HeaderText("Над перекрытием")
            Spacer(Modifier.height(4.dp))
            BuildingRoomOverviewCard(upRoom, true) {
                navigator.navigateToRoom(missionId, upRoom)
            }
        } else {
            HeaderText("Над перекрытием пусто")
        }
    }

    if (!isCeiling) {
        Spacer(Modifier.height(16.dp))
        val downRoom = slab.downRoom
        if (downRoom != null) {
            HeaderText("Под перекрытием")
            Spacer(Modifier.height(4.dp))
            BuildingRoomOverviewCard(downRoom, true){
                navigator.navigateToRoom(missionId, downRoom)
            }
        } else {
            HeaderText("Под перекрытием пусто")
        }
    }
}

@Composable
private fun MakeHoleButton() {
    val missionId = LocalMissionId.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val slab = LocalSlab.current ?: return
    val isDestructible = slab.material.isDestructible

    AutosizeStyledButton(
        title = "Пробить дыру",
        enabled = isDestructible && !slab.hasHole
    ) {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateSlabHole(missionId, slab, true)
        }
    }
}

@Composable
private fun JumpButton() {
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    val viewPoint = LocalViewpointLevel.current
    val slab = LocalSlab.current ?: return
    val isFloor = viewPoint?.locationLevelToFloor() == slab.level
    val legInjury = CharacterDataProvider.character.hasTrait(CharacterTraitsGenerator.LEG_INJURY_TITLE)
    val downRoom = slab.downValidRoom

    AutosizeStyledButton(
        title = "Спрыгнуть в дыру",
        enabled = isFloor && slab.hasHole && downRoom != null && !legInjury
    ) {
        val check = SkillChecksManager.registerHoleJump()

        val successClosure = {
            TimeManager.handleJumpingIntoHole()
            mainLaunch {
                navigator.navigateToRoom(missionId, downRoom!!)
            }
        }
        val successId = ClosuresManager.register(successClosure)

        val failId = ClosuresManager.register {
            val dialogModel = InfoDialogViewModel(
                title = "Ушиб ноги",
                info = "Получена особенность ушиб ноги. Ловкость -5, запрещено спрыгивать в дыры.",
            )

            dialogModel.actions["Принять"] = { isLoading ->
                coroutineLaunch(
                    state = isLoading,
                    task = {
                        val trait = CharacterTraitsGenerator.legInjury()
                        CharacterDataProvider.addTrait(trait)
                    },
                    completion = {
                        successClosure()
                    }
                )
            }

            navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
        }

        navigator.navigateTo(Routes.CharacterSkillCheck, check, successId, failId)
    }
}

@Composable
private fun RemoveHoleButton() {
    val missionId = LocalMissionId.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val slab = LocalSlab.current ?: return

    AutosizeStyledButton(
        title = "Убрать дыру",
        enabled = slab.hasHole
    ) {
        TimeManager.handleHoleMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateSlabHole(missionId, slab, false)
        }
    }
}