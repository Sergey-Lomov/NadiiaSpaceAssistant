package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun EnergyCoreView(device: BuildingDevice, navigator: NavHostController) {
    BuildingDeviceView(device, navigator) {
        AutosizeStyledButton("Ввести малый стержень") {
            TimeManager.energyCoreRodUsage(false)
            showInfo("Малый стержень стабилизации", TimeManager.smallStabilizationBonus, navigator)
        }
        Spacer(Modifier.height(8.dp))
        AutosizeStyledButton("Ввести большой стержень") {
            TimeManager.energyCoreRodUsage(true)
            showInfo("Большой стержень стабилизации", TimeManager.bigStabilizationBonus, navigator)
        }
    }
}

private fun showInfo(rod: String, bonus: Int, navigator: NavHostController) {
    val model = InfoDialogViewModel(
        title = "Стабилизация реактора",
        info = "$rod позволяет отложить перегрузку реактора на $bonus сек"
    )
    model.actions["Принять"] = { navigator.popBackStack() }
    navigator.navigateWithModel(Routes.InfoDialog, model)
}