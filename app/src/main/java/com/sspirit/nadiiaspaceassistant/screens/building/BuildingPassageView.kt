package com.sspirit.nadiiaspaceassistant.screens.building

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingDoorTurn
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassageway
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassagewayType
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.services.ClosuresManager
import com.sspirit.nadiiaspaceassistant.services.PropertyEvacuationTimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.CoroutineLaunch
import kotlinx.coroutines.CoroutineScope

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalPassageState = compositionLocalOf<MutableState<BuildingPassageway>?> { null }

@Composable
fun BuildingPassageView(
    missionId: String,
    locationId: String,
    index: Int,
    navController: NavHostController
) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val location = mission.building.location(locationId) ?: return
    val passage = remember { mutableStateOf(location.passages[index]) }

    ScreenWrapper(navController, "Проем") {
        CompositionLocalProvider(
            LocalMissionId provides missionId,
            LocalPassageState provides passage
        ) {
            ScrollableColumn {
                BuildingPassageCard(passage.value)
                SpacedHorizontalDivider()
                OpenDoorButton(navController)
                AutosizeStyledButton("Взломать дверь") { }
                AutosizeStyledButton("Уничтожить дверь") { }
                AutosizeStyledButton("Уничтожить решетку вентиляции") { }
                AutosizeStyledButton("Пролезть в вентиляцию") { }
                SpacedHorizontalDivider()
                AutosizeStyledButton("Сменить тип") { }
            }
        }
    }
}

@Composable
private fun OpenDoorButton(navController: NavHostController) {
    val passage = LocalPassageState.current ?: return
    val missionId = LocalMissionId.current ?: return
    val isDoorType = passage.value.type == BuildingPassagewayType.DOOR
    val isBroken = passage.value.door?.turn == BuildingDoorTurn.BROKEN

    AutosizeStyledButton(
        title = "Открыть дверь",
        enabled = isDoorType && !isBroken
    ) {
        val door = passage.value.door ?: return@AutosizeStyledButton
        if (door.turn == BuildingDoorTurn.AUTOMATIC) {
            PropertyEvacuationTimeManager.handleDoorOpeningTry(door)
            CoroutineLaunch(
                task = {
                    PropertyEvacuationDataProvider.updatePassageType(missionId, passage.value, BuildingPassagewayType.OPEN_DOOR)
                },
                completion = {
                    passage.value = passage.value
                }
            )
        } else {
            val check = SkillChecksManager.registerDoorOpenCheck(door)
            val successId = ClosuresManager.register { Log.d("Test", "Skill check done") }
            val failId = ClosuresManager.register { Log.d("Test", "Skill check failed") }
            navController.navigateTo(Routes.CharacterSkillCheck, check, successId, failId)
        }
    }
}