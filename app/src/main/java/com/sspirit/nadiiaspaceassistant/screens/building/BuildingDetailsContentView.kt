package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.common.base.Equivalence.Wrapper
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSectorsList
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportsList
import com.sspirit.nadiiaspaceassistant.services.PropertyEvacuationTimeManager
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingDetailsContentViewModel

typealias DataProvider = PropertyEvacuationDataProvider
typealias TimeManager = PropertyEvacuationTimeManager

@Composable
fun BuildingDetailsContentView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingDetailsContentViewModel>(modelId) ?: return
    val mission = PropertyEvacuationDataProvider.getBy(model.missionId) ?: return

    ScreenWrapper(model.parentNavigator, "Комплекс") {
        ScrollableColumn {
            HeaderText("Сектора")
            Spacer(Modifier.height(16.dp))
            BuildingSectorsList(mission.building, mission.id, navigator)
            SpacedHorizontalDivider()
            HeaderText("Транспорт")
            Spacer(Modifier.height(16.dp))
            BuildingTransportsList(mission.building, mission.id, navigator)
        }
    }
}