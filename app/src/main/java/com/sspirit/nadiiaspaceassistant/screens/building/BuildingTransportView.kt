package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

typealias BuildingTransportViewModel = BuildingElementViewModel<BuildingTransport>

@Composable
fun BuildingTransportView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingTransportViewModel>(modelId) ?: return
    val transport = model.element

    ScreenWrapper(navigator, transport.title + "(${transport.id})") {
        ScrollableColumn {
            for (room in transport.rooms) {
                TransportRoomCard(room) {
                    navigator.navigateToRoom(model.missionId, room)
                }
                if (room !== transport.rooms.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TransportRoomCard(room: BuildingRoom, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText("${room.location.sector.title} : ${room.location.title}(${room.location.level}) : ${room.realLocation.string}")
        }
    }
}