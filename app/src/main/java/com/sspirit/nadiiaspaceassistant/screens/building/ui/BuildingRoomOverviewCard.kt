package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun BuildingRoomOverviewCard(room: BuildingRoom, showAddress: Boolean = false, onClick: (() -> Unit)? = null) {
    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            HeaderText(room.realLocation.string)
            if (showAddress)
                TitleValueRow("Адрес", "${room.location.sector.title} : ${room.location.title}")
            TitleValueRow("Тип", room.type)
            TitleValueRow("Свет", humanReadable(room.light))

            if (room.loot.isNotEmpty()) {
                TitleValueRow("Лут", "${room.loot.size}")
            }

            if (room.specLoot.isNotEmpty()) {
                val spec = stringsToList(room.specLoot.map { it.loot.title })
                RegularText("Спец. лут: \n$spec")
            }

            if (room.bigObjects.isNotEmpty()) {
                val objects = room.bigObjects.joinToString("+") {it.size.toString()}
                TitleValueRow("Бол. объекты:", objects)
            }

            if (room.devices.isNotEmpty()) {
                val devices = stringsToList(room.devices.map { it.title })
                RegularText("Устройства: \n$devices")
            }

            if (room.events.isNotEmpty()) {
                val events = stringsToList(room.events.map { it.title })
                RegularText("События: \n$events")
            }

            BuildingTransportRow(room.transports, showIssue = false)
        }
    }
}