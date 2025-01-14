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
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSlabCard
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToCeiling
import com.sspirit.nadiiaspaceassistant.utils.locationLevelToFloor
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

private val LocalModel = compositionLocalOf<BuildingSlabViewModel?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }

typealias BuildingSlabViewModel = RelativeBuildingElementViewModel<BuildingSlab>

@Composable
fun BuildingSlabView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingSlabViewModel>(modelId) ?: return
    val viewPointLevel = model.viewPoint?.location?.level
    val isLoading = remember { mutableStateOf(false) }

    val header = when (model.element.level) {
        viewPointLevel?.locationLevelToFloor() -> "Пол"
        viewPointLevel?.locationLevelToCeiling() -> "Потолок"
        else -> "Перекрытие"
    }

    ScreenWrapper(navigator, header) {
        LoadingOverlay(isLoading) {
            CompositionLocalProvider(
                LocalLoadingState provides isLoading,
                LocalModel provides model,
                LocalNavigator provides navigator
            ) {
                ScrollableColumn {
                    BuildingSlabCard(model.element)
                    Spacer(Modifier.height(8.dp))
                    ConnectedRooms()
                    SpacedHorizontalDivider()
                    MakeHoleButton()
                    Spacer(Modifier.height(8.dp))
                    JumpButton()
                    Spacer(Modifier.height(8.dp))
                    CarefullyDownButton()
                    Spacer(Modifier.height(8.dp))
                    DownByHeapButton()
                    Spacer(Modifier.height(8.dp))
                    UpByHeapButton()
                    SpacedHorizontalDivider()
                    RemoveHoleButton()
                }
            }
        }
    }
}

@Composable
private fun ConnectedRooms() {
    val navigator = LocalNavigator.current ?: return
    val model = LocalModel.current ?: return
    val slab = model.element
    val isFloor = model.viewPoint?.floor == slab
    val isCeiling = model.viewPoint?.ceiling == slab

    SpacedHorizontalDivider()
    if (!isFloor) {
        val upRoom = slab.upRoom
        if (upRoom != null) {
            HeaderText("Над перекрытием")
            Spacer(Modifier.height(4.dp))
            BuildingRoomOverviewCard(upRoom, true) {
                navigator.navigateToRoom(model.missionId, upRoom)
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
                navigator.navigateToRoom(model.missionId, downRoom)
            }
        } else {
            HeaderText("Под перекрытием пусто")
        }
    }
}

@Composable
private fun MakeHoleButton() {
    val model = LocalModel.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val slab = model.element
    val isFloor = model.viewPoint?.floor == slab
    val isCeiling = model.viewPoint?.ceiling == slab
    val isDestructible = slab.material.isDestructible
    val hasHeapLadder = model.viewPoint?.hasLadderHeap ?: false
    val isAccessible = !model.hasViewPoint || isFloor || (isCeiling && hasHeapLadder)

    AutosizeStyledButton(
        title = "Пробить дыру",
        enabled = isDestructible && !slab.hasHole && isAccessible
    ) {
        TimeManager.holeMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateSlabHole(model.missionId, slab, true)
        }
    }
}

@Composable
private fun JumpButton() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val slab = model.element
    val isFloor = model.viewPoint?.floor == slab
    val legInjury = CharacterDataProvider.character.hasTraitType(CharacterTraitType.LEG_INJURY)
    val downRoom = slab.downValidRoom
    val hasHeapLadder = downRoom?.hasLadderHeap ?: false

    AutosizeStyledButton(
        title = "Спрыгнуть в дыру",
        enabled = isFloor && slab.hasHole && downRoom != null && !legInjury && !hasHeapLadder
    ) {
        val onSuccess = {
            TimeManager.jumpingIntoHole()
            mainLaunch {
                navigator.navigateToRoom(model.missionId, downRoom!!)
            }
        }

        val onFail = {
            val trait = CharacterTraitsGenerator.oneDayLegInjury()
            val dialogModel = InfoDialogViewModel.newTrait(trait)
            dialogModel.actions["Принять"] = { isLoading ->
                coroutineLaunch(
                    state = isLoading,
                    task = {
                        CharacterDataProvider.addTrait(trait) { onSuccess() }
                    }
                )
            }

            navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
        }

        val viewModel = CharacterSkillCheckViewModel(
            check = SkillChecksManager.jumpIntoHole(),
            onSuccess = onSuccess,
            onFail = onFail
        )

        navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
    }
}

@Composable
private fun CarefullyDownButton() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val slab = model.element
    val isFloor = model.viewPoint?.floor == slab
    val downRoom = slab.downValidRoom
    val hasHeapLadder = downRoom?.hasLadderHeap ?: false

    AutosizeStyledButton(
        title = "Аккуратно слезть в дыру",
        enabled = isFloor && slab.hasHole && downRoom != null && !hasHeapLadder
    ) {
        TimeManager.carefullyDownIntoHole()
        navigator.navigateToRoom(model.missionId, downRoom!!)
    }
}

@Composable
private fun DownByHeapButton() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val slab = model.element
    val isFloor = model.viewPoint?.floor == slab
    val downRoom = slab.downValidRoom
    val hasHeapLadder = downRoom?.hasLadderHeap ?: false

    AutosizeStyledButton(
        title = "Слезть в дыру по куче",
        enabled = isFloor && slab.hasHole && downRoom != null && hasHeapLadder
    ) {
        TimeManager.downByHeap()
        navigator.navigateToRoom(model.missionId, downRoom!!)
    }
}

@Composable
private fun UpByHeapButton() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val slab = model.element
    val isCeiling = model.viewPoint?.ceiling == slab
    val upRoom = slab.upValidRoom
    val hasHeapLadder =  model.viewPoint?.hasLadderHeap ?: false

    AutosizeStyledButton(
        title = "Вылезти в дыру по куче",
        enabled = isCeiling && slab.hasHole && upRoom != null && hasHeapLadder
    ) {
        TimeManager.upByHeap()
        navigator.navigateToRoom(model.missionId, upRoom!!)
    }
}

@Composable
private fun RemoveHoleButton() {
    val model = LocalModel.current ?: return
    val slab = model.element
    val loadingState = LocalLoadingState.current ?: return

    AutosizeStyledButton(
        title = "Убрать дыру",
        enabled = slab.hasHole
    ) {
        TimeManager.holeMaking()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updateSlabHole(model.missionId, slab, false)
        }
    }
}