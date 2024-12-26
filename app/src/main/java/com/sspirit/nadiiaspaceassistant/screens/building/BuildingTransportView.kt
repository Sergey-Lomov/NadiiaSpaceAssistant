package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun BuildingTransportView(missionId: String, index: Int, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val transport = mission.building.transports[index]

    ScreenWrapper(navController, transport.title + "($index)") {
        ScrollableColumn {
            for (room in transport.rooms) {
                TransportRoomCard(room, navController)
                if (room !== transport.rooms.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TransportRoomCard(room: BuildingRoom, navController: NavHostController) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText("${room.location.sector.title} -> ${room.location.title}(${room.location.level}) -> ${room.realLocation.string}")
        }
    }
}