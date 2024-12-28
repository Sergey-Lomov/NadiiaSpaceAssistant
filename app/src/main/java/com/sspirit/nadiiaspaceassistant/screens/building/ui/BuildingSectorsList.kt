package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.navigation.Routes

@Composable
fun BuildingSectorsList(building: Building, missionId: String, navController: NavHostController) {
    if (building.sectors.isEmpty()) return

    Column {
        for(sector in building.sectors) {
            BuildingSectorCard(sector, building.transports) {
                val index = building.sectors.indexOf(sector)
                navController.navigateTo(Routes.BuildingSectorDetails, missionId, index)
            }
            if (sector != building.sectors.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}