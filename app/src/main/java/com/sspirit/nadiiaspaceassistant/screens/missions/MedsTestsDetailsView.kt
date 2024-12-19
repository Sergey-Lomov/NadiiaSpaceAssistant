package com.sspirit.nadiiaspaceassistant.screens.missions

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider

@Composable
fun MedsTestsDetailsView(id: String, navController: NavHostController) {
    MissionDetailsView(id, MedsTestsDataProvider, navController)
}