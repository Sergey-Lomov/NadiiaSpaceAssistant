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
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn

@Composable
fun BuildingTransportView(missionId: String, transportId: String, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val transport = mission.building.transport(transportId) ?: return

    ScreenWrapper(navController, transport.title + "($transportId)") {
        ScrollableColumn {
            for (room in transport.rooms) {
                TransportRoomCard(room) {
                    navController.navigateTo(Routes.BuildingRoomDetails, missionId, room.location.id, room.realLocation)
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
            HeaderText("${room.location.sector.title} -> ${room.location.title}(${room.location.level}) -> ${room.realLocation.string}")
        }
    }
}