package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun BuildingSlabView(
    missionId: String,
    sectorTitle: String,
    level: Float,
    realLocation: RealLifeLocation,
    navController: NavHostController
) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val sector = mission.building.sector(sectorTitle) ?: return
    val slab = sector.slabs[level]?.firstOrNull { it.realLocation == realLocation } ?: return

    ScreenWrapper(navController, "Перекрытие") {
        ScrollableColumn {
            BuildingSlabCard(slab)
        }
    }
}