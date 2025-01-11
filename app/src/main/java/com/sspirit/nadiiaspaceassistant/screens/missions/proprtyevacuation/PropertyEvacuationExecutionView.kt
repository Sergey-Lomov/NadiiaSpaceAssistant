package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.BuildingNavigation
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingDetailsContentViewModel

@Composable
fun PropertyEvacuationExecutionView(missionId: String, navigator: NavHostController) {
    Column {
        val model = BuildingDetailsContentViewModel(missionId, navigator)
        val modelId = ViewModelsRegister.register(model)
        Box(Modifier.weight(1f)) {
            BuildingNavigation(modelId)
        }
        PropertyEvacuationStatusPanel(missionId, navigator)
    }
}