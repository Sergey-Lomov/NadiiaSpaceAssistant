package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLocationCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSectorCard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider

@Composable
fun BuildingSectorView(missionId: String, index: Int, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val sector = mission.building.sectors[index]

    ScreenWrapper(navController, sector.title) {
        ScrollableColumn {
            BuildingSectorCard(sector, mission.building.transports, false)
            SpacedHorizontalDivider()
            for (location in sector.locations) {
                BuildingLocationCard(location) {
                    navController.navigateTo(Routes.BuildingLocationDetails, missionId, location.id)
                }
                if (location !== sector.locations.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}