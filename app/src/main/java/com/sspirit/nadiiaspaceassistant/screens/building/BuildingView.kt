package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
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