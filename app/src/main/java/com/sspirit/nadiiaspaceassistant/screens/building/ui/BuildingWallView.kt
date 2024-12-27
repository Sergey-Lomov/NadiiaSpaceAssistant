package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun BuildingWallView(
    missionId: String,
    locationId: String,
    index: Int,
    navController: NavHostController
) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val location = mission.building.location(locationId) ?: return
    val wall = location.walls[index]

    ScreenWrapper(navController, "Стена") {
        ScrollableColumn {
            BuildingWallCard(wall)
        }
    }
}