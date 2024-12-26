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
import com.sspirit.nadiiaspaceassistant.extensions.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow


@Composable
fun BuildingTransportsList(building: Building, missionId: String, navController: NavHostController) {
    if (building.sectors.isEmpty()) return

    Column {
        for(transport in building.transports) {
            val index = building.transports.indexOf(transport)
            TransportCard(transport, index) {
                navController.navigateTo(Routes.BuildingTransportDetails, missionId ,index)
            }
            if (transport != building.transports.last()) {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TransportCard(transport: BuildingTransport, index: Int, onClick: () -> Unit) {
    Card(onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText(transport.title + "($index)")
            TitleValueRow("Мест: ", "${transport.rooms.size}", 18)
        }
    }
}