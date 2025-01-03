package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel

@Composable
fun SafetyConsoleView(
    missionId: String,
    room: BuildingRoom,
    device: BuildingDevice.SafetyConsole,
    navigator: NavHostController
) {
    BuildingDeviceView(
        device = device,
        navigator = navigator,
        info = mapOf("Взломана" to { humanReadable(device.hacked) })
    ) { state ->
        if (device.hacked) {
            AutosizeStyledButton("Деактивировать все замки") {
                val building = DataProvider.getBy(missionId)?.building ?: return@AutosizeStyledButton
                simpleCoroutineLaunch(state) {
                    DataProvider.removeAllRemoteLocks(missionId, building)
                }
            }
        } else {
            AutosizeStyledButton("Взломать") {
                val onSuccess = {
                    simpleCoroutineLaunch(state) {
                        DataProvider.updateSafetyConsoleHacked(
                            missionId = missionId,
                            location = room.location,
                            console = device,
                            hacked = true) {
                            if (it) {
                                TimeManager.safetyConsoleHackingTry()
                                mainLaunch { navigator.popBackStack() }
                            }
                        }
                    }
                }

                val viewModel = CharacterSkillCheckViewModel(
                    check = SkillChecksManager.hackSafetyConsole(),
                    onSuccess = onSuccess,
                    onFail = { TimeManager.safetyConsoleHackingTry() }
                )

                navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
            }
        }
    }
}