package com.sspirit.nadiiaspaceassistant.screens.missions.medstests

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider

@Composable
fun MedsTestsDetailsView(id: String, navController: NavHostController) {
    MissionDetailsView(id, MedsTestsDataProvider, navController) {
        navController.navigate(Routes.MedsTestsExecution.route + "/$id")
    }
}