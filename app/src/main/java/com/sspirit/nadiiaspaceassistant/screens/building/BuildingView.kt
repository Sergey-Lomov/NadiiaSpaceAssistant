package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSectorsList
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportsList
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider

@Composable
fun BuildingView(missionId: String, navController: NavHostController) {
    ScreenWrapper(navController, "Объект") {
        val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return@ScreenWrapper
        ScrollableColumn {
            HeaderText("Сектора")
            Spacer(Modifier.height(16.dp))
            BuildingSectorsList(mission.building, missionId, navController)
            SpacedHorizontalDivider()
            HeaderText("Транспорт")
            Spacer(Modifier.height(16.dp))
            BuildingTransportsList(mission.building, missionId, navController)
        }
    }
}