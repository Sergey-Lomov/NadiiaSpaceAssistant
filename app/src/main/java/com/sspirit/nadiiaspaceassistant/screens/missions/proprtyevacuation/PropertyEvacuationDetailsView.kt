package com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider

@Composable
fun PropertyEvacuationDetailsView(id: String, navController: NavHostController) {
    MissionDetailsView(id, PropertyEvacuationDataProvider, navController) {
        //navController.navigate(Routes.MedsTestsExecution.route + "/$id")
    }
}