package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun BuildingLocationCard(
    location: BuildingLocation,
    full: Boolean = false,
    showHeader: Boolean = true,
    onClick: (() -> Unit)? = null) {
    Card (
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showHeader)
                HeaderText(location.title)
            TitleValueRow("Id/Этаж", "${location.id} : ${location.level}")
            TitleValueRow("Тип", location.type.string)

            val loot = location.rooms.flatMap { it.loot.asIterable() }
            val specLoot = location.rooms.flatMap { it.specLoot.asIterable() }
            val devices = location.rooms.flatMap { it.devices.asIterable() }
            val events = location.rooms.flatMap { it.events.asIterable() }

            if (loot.isNotEmpty()) {
                TitleValueRow("Лут", "${loot.size}")
            }

            if (specLoot.isNotEmpty()) {
                val spec = stringsToList(specLoot.map { it.title })
                RegularText("Спец. лут: \n$spec")
            }

            if (devices.isNotEmpty()) {
                if (full) {
                    val details = stringsToList(devices.map { it.string })
                    RegularText("Устройства: \n$details")
                } else {
                    TitleValueRow("Устройства", devices.size.toString())
                }
            }

            if (events.isNotEmpty()) {
                if (full) {
                    val details = stringsToList(events.map { it.string })
                    RegularText("События: $details")
                } else {
                    TitleValueRow("События", events.size.toString())
                }
            }

            val transports = location.rooms
                .flatMap { it.transports.asIterable() }
                .distinct()
                .toTypedArray()
            BuildingTransportRow(transports, "В локации нет ранспорта")
        }
    }
}