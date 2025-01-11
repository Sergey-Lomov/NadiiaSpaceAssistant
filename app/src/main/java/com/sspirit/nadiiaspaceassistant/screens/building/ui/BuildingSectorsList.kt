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
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSectorViewModel
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel

@Composable
fun BuildingSectorsList(building: Building, missionId: String, navigator: NavHostController) {
    if (building.sectors.isEmpty()) return

    Column {
        for(sector in building.sectors) {
            BuildingSectorCard(sector, building.transports) {
                val model = BuildingSectorViewModel(missionId, sector)
                navigator.navigateWithModel(BuildingRoutes.SectorDetails, model)
            }
            if (sector != building.sectors.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}