package com.sspirit.nadiiaspaceassistant.screens.missions.energylines

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.EnergyLinesDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider

@Composable
fun EnergyLinesDetailsView(id: String, navController: NavHostController) {
    MissionDetailsView(id, EnergyLinesDataProvider, navController) {
        navController.navigate(Routes.EnergyLinesExecution.route + "/$id")
    }
}