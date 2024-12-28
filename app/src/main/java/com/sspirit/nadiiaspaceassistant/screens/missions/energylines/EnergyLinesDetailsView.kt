package com.sspirit.nadiiaspaceassistant.screens.missions.energylines

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.EnergyLinesDataProvider

@Composable
fun EnergyLinesDetailsView(id: String, navController: NavHostController) {
    MissionDetailsView(id, EnergyLinesDataProvider, navController, {
        navController.navigateTo(Routes.EnergyLinesExecution, id)
    }
    )
}