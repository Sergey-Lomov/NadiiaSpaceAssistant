package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice.*
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingDeviceViewModel

@Composable
fun BuildingDeviceRouterView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingDeviceViewModel>(modelId) ?: return
    val missionId = model.missionId
    val room = model.room

    when (val device = model.device) {
        is AcidTank -> AcidTankView(missionId, room, device, navigator)
        AutoDoctor -> AutoDoctorView(device, navigator)
        EnergyCore -> EnergyCoreView(device, navigator)
        is EnergyNode -> EnergyNodeView(missionId, room, device, navigator)
        is HoloPlan -> HoloPlanView(missionId, device, navigator)
        Mainframe -> MainframeView(missionId, device, navigator)
        is SafetyConsole -> SafetyConsoleView(missionId, room, device, navigator)
        SupportConsole -> SupportConsoleView(missionId, device, navigator)
        Undefined -> HeaderText("Тип устройства не определен")
    }
}