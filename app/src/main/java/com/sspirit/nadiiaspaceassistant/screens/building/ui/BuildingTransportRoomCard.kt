package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList

@Composable
fun BuildingTransportRoomCard(
    transport: BuildingTransport,
    room: BuildingRoom,
    viewpoint: BuildingRoom?,
    onClick: (() -> Unit)? = null) {

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (transport is BuildingElevator) {
                CenteredRegularText("Этаж ${room.location.level}: ${room.location.title}")
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Положение", room.realLocation.string)
            } else {
                TitlesValuesList(
                    "Сектор" to room.location.sector.title,
                    "Локация" to "${room.location.title}(${room.location.level})",
                    "Комната" to room.realLocation.string,
                )
            }

            if (viewpoint != null) {
                if (viewpoint != room) {
                    val duration = transport.timeCost(viewpoint, room)
                    Spacer(Modifier.height(8.dp))
                    TitleValueRow("Продолжительность", duration.toString())
                } else {
                    CenteredRegularText("Текущая комната")
                }
            }
        }
    }
}