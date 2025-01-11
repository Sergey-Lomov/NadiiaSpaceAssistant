package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLocationCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSectorCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

typealias BuildingSectorViewModel = BuildingElementViewModel<BuildingSector>

@Composable
fun BuildingSectorView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingSectorViewModel>(modelId) ?: return
    val mission = PropertyEvacuationDataProvider.getBy(model.missionId) ?: return
    val sector = model.element

    ScreenWrapper(navigator, sector.title) {
        ScrollableColumn {
            BuildingSectorCard(sector, mission.building.transports, false)
            SpacedHorizontalDivider()
            for (location in sector.locations) {
                BuildingLocationCard(location) {
                    val viewModel = BuildingLocationViewModel(model.missionId, location)
                    navigator.navigateWithModel(BuildingRoutes.LocationDetails, viewModel)
                }
                if (location !== sector.locations.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}