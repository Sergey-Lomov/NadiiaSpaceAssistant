package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun SupportConsoleView(missionId: String, device: BuildingDevice, navigator: NavHostController) {
    val building = DataProvider.getBy(missionId)?.building ?: return

    BuildingDeviceView(device, navigator) { state ->
        AutosizeStyledButton("Поднять вент. решетки") {
            simpleCoroutineLaunch(state) {
                DataProvider.updateAllVentGrille(missionId, building, BuildingVentGrilleState.UP) {
                    if (it) TimeManager.ventUnlockedByConsole()
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        AutosizeStyledButton("Опустить вент. решетки") {
            simpleCoroutineLaunch(state) {
                DataProvider.updateAllVentGrille(missionId, building, BuildingVentGrilleState.DOWN) {
                    if (it) TimeManager.ventLockedByConsole()
                }
            }
        }
    }
}