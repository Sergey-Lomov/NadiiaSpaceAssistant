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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

private val LocalModel = compositionLocalOf<BuildingPassageViewModel?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

typealias BuildingPassageViewModel = RelativeBuildingElementViewModel<BuildingPassage>

@Composable
fun BuildingPassageView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingPassageViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Проем") {
        CompositionLocalProvider(
            LocalModel provides model,
            LocalLoadingState provides isLoading,
            LocalNavigator provides navigator
        ) {
            LoadingOverlay(isLoading) {
                ScrollableColumn {
                    BuildingPassageCard(model.element)
                    LockedText()
                    ConnectedRooms()
                    SpacedHorizontalDivider()
                    SwitchDoorButton()
                    Spacer(Modifier.height(8.dp))
                    HackDoorButton()
                    Spacer(Modifier.height(8.dp))
                    DestroyDoorButton()
                    Spacer(Modifier.height(8.dp))
                    RemoveGrilleButton()
                    Spacer(Modifier.height(8.dp))
                    CrawlVentButton()
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun LockedText() {
    val model = LocalModel.current ?: return

    if (model.element.isPassable && model.element.isLockedByHeap) {
        Spacer(Modifier.height(8.dp))
        CenteredRegularText("Проход заблокирован кучей больших объектов", colorResource(R.color.soft_red))
    }
}

@Composable
private fun ConnectedRooms() {
    val model = LocalModel.current ?: return
    val navigator = LocalNavigator.current ?: return
    val rooms = model.element.rooms
        .filter { it != model.viewPoint }

    SpacedHorizontalDivider()
    for (room in rooms) {
        BuildingRoomOverviewCard(room, true) {
            navigator.navigateToRoom(model.missionId, room)
        }
    }
}

@Composable
private fun SwitchDoorButton() {
    val model = LocalModel.current ?: return
    val isOpenDoor = model.element.type == BuildingPassagewayType.OPEN_DOOR
    if (isOpenDoor)
        CloseDoorButton()
    else
        OpenDoorButton()
}

@Composable
private fun CloseDoorButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val loadingState = LocalLoadingState.current ?: return
    val isOpenDoor = passage.type == BuildingPassagewayType.OPEN_DOOR
    val isBroken = passage.door?.turn == BuildingDoorTurn.BROKEN

    AutosizeStyledButton(
        title = "Закрыть дверь",
        enabled = isOpenDoor && !isBroken
    ) {
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updatePassageType(model.missionId, passage, BuildingPassagewayType.DOOR) {
                TimeManager.doorClosing()
            }
        }
    }
}

@Composable
private fun OpenDoorButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val loadingState = LocalLoadingState.current ?: return
    val navigator = LocalNavigator.current ?: return
    val isClosedDoor = passage.type == BuildingPassagewayType.DOOR
    val isBroken = passage.door?.turn == BuildingDoorTurn.BROKEN

    AutosizeStyledButton(
        title = "Открыть дверь",
        enabled = isClosedDoor && !isBroken
    ) {
        val door = passage.door ?: return@AutosizeStyledButton
        if (door.turn == BuildingDoorTurn.AUTOMATIC) {
            requestDoorOpening(model.missionId, passage, loadingState) {
                if (it) TimeManager.doorOpeningTry(door)
            }
        } else {
            val viewModel = CharacterSkillCheckViewModel(
                check = SkillChecksManager.openDoor(door),
                onSuccess = {
                    mainLaunch { navigator.popBackStack() }
                    requestDoorOpening(model.missionId, passage, loadingState) {
                        if (it) TimeManager.doorOpeningTry(door)
                    }
                },
                onFail = {
                    TimeManager.doorOpeningTry(door)
                }
            )

            navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
        }
    }
}

private fun requestDoorOpening(
    missionId: String,
    passage: BuildingPassage,
    loadingState: MutableState<Boolean>,
    completion: (Boolean) -> Unit
) {
    simpleCoroutineLaunch (loadingState) {
        DataProvider.updatePassageType(missionId, passage, BuildingPassagewayType.OPEN_DOOR, completion)
    }
}

@Composable
private fun HackDoorButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val navigator = LocalNavigator.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val isDoorType = passage.type == BuildingPassagewayType.DOOR
    val hasLocks = passage.door?.locks?.isNotEmpty() ?: false
    val isHackable = passage.door?.hacking != BuildingDoorHackingLevel.UNHACKABLE

    AutosizeStyledButton(
        title = "Хакнуть замки",
        enabled = isDoorType && isHackable && hasLocks
    ) {
        val door = passage.door ?: return@AutosizeStyledButton
        val viewModel = CharacterSkillCheckViewModel(
            check = SkillChecksManager.hackDoor(door),
            onSuccess = {
                mainLaunch { navigator.popBackStack() }
                simpleCoroutineLaunch (loadingState) {
                    DataProvider.updatePassageLocks(model.missionId, passage, arrayOf()) {
                        if (it) TimeManager.doorHackingTry()
                    }
                }
            },
            onFail = {
                TimeManager.doorHackingTry()
            }
        )

        navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
    }
}

@Composable
private fun DestroyDoorButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val loadingState = LocalLoadingState.current ?: return
    val isDoorType = passage.type in arrayOf(BuildingPassagewayType.DOOR, BuildingPassagewayType.OPEN_DOOR)
    val isDestructible = passage.door?.isDestructible ?: false

    AutosizeStyledButton(
        title = "Уничтожить дверь",
        enabled = isDoorType && isDestructible
    ) {
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updatePassageType(model.missionId, passage, BuildingPassagewayType.HOLE) {
                if (it) TimeManager.doorDestruction()
            }
        }
    }
}

@Composable
private fun RemoveGrilleButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val loadingState = LocalLoadingState.current ?: return
    val hasGrille = passage.vent?.grilleState in arrayOf(BuildingVentGrilleState.DOWN, BuildingVentGrilleState.UP)

    AutosizeStyledButton(
        title = "Убрать вент. решетку",
        enabled = hasGrille
    ) {
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updatePassageVentGrille(model.missionId, passage, BuildingVentGrilleState.MISSED) {
                if (it) TimeManager.ventGrilleRemoving()
            }
        }
    }
}

@Composable
private fun CrawlVentButton() {
    val model = LocalModel.current ?: return
    val passage = model.element
    val navigator = LocalNavigator.current ?: return
    val isCrawlable = passage.vent?.size?.isCrawable ?: false
    val noGrille = passage.vent?.grilleState != BuildingVentGrilleState.DOWN

    AutosizeStyledButton(
        title = "Пролезть в вентиляцию",
        enabled = isCrawlable && noGrille
    ) {
        val vent = passage.vent ?: return@AutosizeStyledButton
        val viewModel = CharacterSkillCheckViewModel(
            check = SkillChecksManager.crawlVent(vent),
            onSuccess = {
                TimeManager.ventCrawlingTry()
                mainLaunch {
                    val room = model.viewPoint
                    if (room != null)
                        navigator.navigateToRoom(
                            missionId = model.missionId,
                            room = passage.anotherRoom(room)
                        )
                    else
                        navigator.popBackStack()
                }
            },
            onFail = {
                TimeManager.ventCrawlingTry()
            }
        )

        navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
    }
}