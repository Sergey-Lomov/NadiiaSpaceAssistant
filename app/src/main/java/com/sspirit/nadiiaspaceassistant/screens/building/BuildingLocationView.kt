package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLocationCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

typealias BuildingLocationViewModel = BuildingElementViewModel<BuildingLocation>

@Composable
fun BuildingLocationView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingLocationViewModel>(modelId) ?: return
    val location = model.element

    ScreenWrapper(navigator, location.title) {
        ScrollableColumn {
            BuildingLocationCard(location, full = true, showHeader = false)
            SpacedHorizontalDivider()
            for (room in location.rooms) {
                BuildingRoomOverviewCard(room) {
                    val viewModel = BuildingRoomViewModel(model.missionId, room)
                    navigator.navigateWithModel(BuildingRoutes.RoomDetails, viewModel)
                }
                if (room !== location.rooms.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}
