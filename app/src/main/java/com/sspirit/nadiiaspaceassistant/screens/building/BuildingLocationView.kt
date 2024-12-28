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
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLocationCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportRow
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun BuildingLocationView(missionId: String, locationId: String, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val location = mission.building.location(locationId) ?: return

    ScreenWrapper(navController, location.title) {
        ScrollableColumn {
            BuildingLocationCard(location, full = true, showHeader = false)
            SpacedHorizontalDivider()
            for (room in location.rooms) {
                BuildingRoomCard(room) {
                    navController.navigateTo(Routes.BuildingRoomDetails, missionId, locationId, room.realLocation)
                }
                if (room !== location.rooms.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BuildingRoomCard(room: BuildingRoom, onClick: (() -> Unit)? = null) {
    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText(room.type)
            TitleValueRow("Положение", room.realLocation.string)
            TitleValueRow("Свет", humanReadable(room.light))

            if (room.loot.isNotEmpty()) {
                TitleValueRow("Лут", "${room.loot.size} : ${room.specLoot.size}")
            }

            if (room.specLoot.isNotEmpty()) {
                val spec = stringsToList(room.specLoot.map { it.title })
                RegularText("Спец. лут: \n$spec")
            }

            if (room.devices.isNotEmpty()) {
                val devices = stringsToList(room.devices.map { it.string })
                RegularText("Устройства: \n$devices")
            }

            if (room.events.isNotEmpty()) {
                val events = stringsToList(room.events.map { it.string })
                RegularText("События: \n$events")
            }

            BuildingTransportRow(room.transports, showIssue = false)
        }
    }
}