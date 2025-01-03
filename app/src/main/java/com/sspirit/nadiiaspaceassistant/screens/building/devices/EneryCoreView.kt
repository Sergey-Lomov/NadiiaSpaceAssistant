package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton

@Composable
fun EnergyCoreView(device: BuildingDevice, navigator: NavHostController) {
    BuildingDeviceView(device, navigator) {
        AutosizeStyledButton("Ввести малый стержень") {
            TimeManager.energyCoreRodUsage(false)
        }
        Spacer(Modifier.height(8.dp))
        AutosizeStyledButton("Ввести большой стержень") {
            TimeManager.energyCoreRodUsage(true)
        }
    }
}