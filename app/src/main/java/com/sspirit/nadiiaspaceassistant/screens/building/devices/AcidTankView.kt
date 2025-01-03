package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun AcidTankView(
    missionId: String,
    room: BuildingRoom,
    device: BuildingDevice.AcidTank,
    navigator: NavHostController
) {
    BuildingDeviceView(
        device = device,
        info = mapOf("Уровень кислоты" to { device.charges }),
        navigator = navigator
    ) { state ->
        val hasCharge = device.charges > 0
        AutosizeStyledButton(
            title = "Зарядить",
            enabled = hasCharge
        ) {
            TimeManager.acidChargeRecharge()
            val charges = device.charges - 1
            simpleCoroutineLaunch(state) {
                DataProvider.updateAcidTankCharges(missionId, room.location, device, charges)
            }
        }
    }
}