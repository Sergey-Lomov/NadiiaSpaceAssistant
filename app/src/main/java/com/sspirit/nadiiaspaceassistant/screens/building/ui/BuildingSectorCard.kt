package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSector
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun BuildingSectorCard(
    sector: BuildingSector,
    transports: Array<BuildingTransport>,
    showHeader: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val rooms = sector.locations
        .flatMap { it.rooms.asIterable() }
        .toSet()
    val filteredTransports = transports
        .filter { it.rooms.intersect(rooms).isNotEmpty() }
        .toTypedArray()

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (showHeader)
                HeaderText(sector.title)
            TitleValueRow("Локаций: ", "${sector.locations.size}", 18)
            BuildingTransportRow(transports, "В секторе нет транспорта")
        }
    }
}