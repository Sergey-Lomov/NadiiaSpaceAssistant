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
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorHackingLevel
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassageway
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentSize
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.services.ClosuresManager
import com.sspirit.nadiiaspaceassistant.services.PropertyEvacuationTimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.LoadingIndicator
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalPassage = compositionLocalOf<BuildingPassageway?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

@Composable
fun BuildingPassageView(
    missionId: String,
    locationId: String,
    index: Int,
    navController: NavHostController
) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val location = mission.building.location(locationId) ?: return
    val passage = location.passages[index]
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navController, "Проем") {
        CompositionLocalProvider(
            LocalMissionId provides missionId,
            LocalPassage provides passage,
            LocalLoadingState provides isLoading
        ) {
            if (isLoading.value)
                LoadingIndicator()
            else {
                ScrollableColumn {
                    BuildingPassageCard(passage)
                    SpacedHorizontalDivider()
                    OpenDoorButton(navController)
                    Spacer(Modifier.height(8.dp))
                    HackDoorButton(navController)
                    Spacer(Modifier.height(8.dp))
                    DestroyDoorButton()
                    Spacer(Modifier.height(8.dp))
                    RemoveGrilleButton()
                    Spacer(Modifier.height(8.dp))
                    CrawlVentButton(navController)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun OpenDoorButton(navController: NavHostController) {
    val passage = LocalPassage.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val missionId = LocalMissionId.current ?: return
    val isClosedDoor = passage.type == BuildingPassagewayType.DOOR
    val isBroken = passage.door?.turn == BuildingDoorTurn.BROKEN

    AutosizeStyledButton(
        title = "Открыть дверь",
        enabled = isClosedDoor && !isBroken
    ) {
        val door = passage.door ?: return@AutosizeStyledButton
        if (door.turn == BuildingDoorTurn.AUTOMATIC) {
            TimeManager.handleDoorOpeningTry(door)
            requestDoorOpening(missionId, passage, loadingState)
        } else {
            val check = SkillChecksManager.registerDoorOpenCheck(door)
            val successId = ClosuresManager.register {
                TimeManager.handleDoorOpeningTry(door)
                mainLaunch { navController.popBackStack() }
                requestDoorOpening(missionId, passage, loadingState)
            }
            val failId = ClosuresManager.register {
                TimeManager.handleDoorOpeningTry(door)
            }
            navController.navigateTo(Routes.CharacterSkillCheck, check, successId, failId)
        }
    }
}

private fun requestDoorOpening(
    missionId: String,
    passage: BuildingPassageway,
    loadingState: MutableState<Boolean>
) {
    simpleCoroutineLaunch (loadingState) {
        DataProvider.updatePassageType(missionId, passage, BuildingPassagewayType.OPEN_DOOR)
    }
}

@Composable
private fun HackDoorButton(navController: NavHostController) {
    val passage = LocalPassage.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val missionId = LocalMissionId.current ?: return
    val isDoorType = passage.type == BuildingPassagewayType.DOOR
    val hasLocks = passage.door?.locks?.isNotEmpty() ?: false
    val isHackable = passage.door?.hacking != BuildingDoorHackingLevel.UNHACKABLE

    AutosizeStyledButton(
        title = "Хакнуть замки",
        enabled = isDoorType && isHackable && hasLocks
    ) {
        val door = passage.door ?: return@AutosizeStyledButton
        val check = SkillChecksManager.registerDoorHackCheck(door)
        val successId = ClosuresManager.register {
            TimeManager.handleDoorHackingTry()
            mainLaunch { navController.popBackStack() }
            simpleCoroutineLaunch (loadingState) {
                DataProvider.updatePassageLocks(missionId, passage, arrayOf())
            }
        }
        val failId = ClosuresManager.register {
            TimeManager.handleDoorHackingTry()
        }
        navController.navigateTo(Routes.CharacterSkillCheck, check, successId, failId)
    }
}

@Composable
private fun DestroyDoorButton() {
    val passage = LocalPassage.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val missionId = LocalMissionId.current ?: return
    val isDoorType = passage.type in arrayOf(BuildingPassagewayType.DOOR, BuildingPassagewayType.OPEN_DOOR)
    val isDestructible = passage.door?.isDestructible ?: false

    AutosizeStyledButton(
        title = "Уничтожить дверь",
        enabled = isDoorType && isDestructible
    ) {
        TimeManager.handleDoorDestruction()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updatePassageType(missionId, passage, BuildingPassagewayType.HOLE)
        }
    }
}

@Composable
private fun RemoveGrilleButton() {
    val passage = LocalPassage.current ?: return
    val loadingState = LocalLoadingState.current ?: return
    val missionId = LocalMissionId.current ?: return
    val hasGrille = passage.vent?.grilleState in arrayOf(BuildingVentGrilleState.DOWN, BuildingVentGrilleState.UP)

    AutosizeStyledButton(
        title = "Убрать вент. решетку",
        enabled = hasGrille
    ) {
        TimeManager.handleVentGrilleRemoving()
        simpleCoroutineLaunch (loadingState) {
            DataProvider.updatePassageVentGrille(missionId, passage, BuildingVentGrilleState.MISSED)
        }
    }
}

@Composable
private fun CrawlVentButton(navController: NavHostController) {
    val passage = LocalPassage.current ?: return
    val isCrawlable = passage.vent?.size?.isCrawable ?: false
    val noGrille = passage.vent?.grilleState != BuildingVentGrilleState.DOWN

    AutosizeStyledButton(
        title = "Пролезть в вентиляцию",
        enabled = isCrawlable && noGrille
    ) {
        val vent = passage.vent ?: return@AutosizeStyledButton
        val check = SkillChecksManager.registerVentCrawlCheck(vent)
        val successId = ClosuresManager.register {
            TimeManager.handleVentCrawlingTry()
            mainLaunch { navController.popBackStack() }
        }
        val failId = ClosuresManager.register {
            TimeManager.handleVentCrawlingTry()
        }
        navController.navigateTo(Routes.CharacterSkillCheck, check, successId, failId)
    }
}