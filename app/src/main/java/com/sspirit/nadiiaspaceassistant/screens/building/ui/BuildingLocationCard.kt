package com.sspirit.nadiiaspaceassistant.screens.building.ui

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
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap

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

            val loot = location.rooms.flatArrayMap { it.loot }
            val specLoot = location.rooms.flatArrayMap { it.specLoot }
            val devices = location.rooms.flatArrayMap { it.devices }
            val events = location.rooms.flatArrayMap { it.events }

            if (loot.isNotEmpty()) {
                TitleValueRow("Лут", "${loot.size}")
            }

            if (specLoot.isNotEmpty()) {
                val spec = stringsToList(specLoot.map { it.loot.title })
                RegularText("Спец. лут: \n$spec")
            }

            if (devices.isNotEmpty()) {
                if (full) {
                    val details = stringsToList(devices.map { it.title })
                    RegularText("Устройства: \n$details")
                } else {
                    TitleValueRow("Устройства", devices.size.toString())
                }
            }

            if (events.isNotEmpty()) {
                if (full) {
                    val details = stringsToList(events.map { it.title })
                    RegularText("События: \n$details")
                } else {
                    TitleValueRow("События", events.size.toString())
                }
            }

            val transports = location.rooms
                .flatArrayMap { it.transports }
                .distinct()
                .toTypedArray()
            BuildingTransportRow(transports, "В локации нет транспорта")
        }
    }
}