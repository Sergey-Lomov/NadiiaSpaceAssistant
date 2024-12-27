package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun BuildingPassageView(
    missionId: String,
    locationId: String,
    index: Int,
    navController: NavHostController
) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val location = mission.building.location(locationId) ?: return
    val passage = location.passages[index]

    ScreenWrapper(navController, "Проем") {
        ScrollableColumn {
            BuildingPassageCard(passage)
        }
    }
}