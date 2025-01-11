package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.BuildingNavigation
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingDetailsContentViewModel

@Composable
fun BuildingDetailsView(missionId: String, navigator: NavHostController) {
    val model = BuildingDetailsContentViewModel(missionId, navigator)
    val modelId = ViewModelsRegister.register(model)
    BuildingNavigation(modelId)
}