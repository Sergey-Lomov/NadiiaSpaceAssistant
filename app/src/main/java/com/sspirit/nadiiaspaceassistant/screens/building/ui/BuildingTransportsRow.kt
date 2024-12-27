package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingShuttlePod
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTeleport
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.RegularText

@Composable
fun BuildingTransportRow(
    transports: Array<BuildingTransport>,
    issue: String = "Нет транспорта",
    showIssue: Boolean = true
) {
    if (transports.isNotEmpty())
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            RegularText("Транспорт", false)
            Spacer(Modifier.weight(1f))
            TransportCircles(transports)
        }
    else if (showIssue)
        Row {
            ColoredCircle(colorResource(R.color.soft_red), 20)
            Spacer(Modifier.width(16.dp))
            RegularText(issue)
        }
}

@Composable
fun TransportCircles(transports: Array<BuildingTransport>) {
    Row {
        for (transport in transports) {
            ColoredCircle(colors(transport), 20, transport.id, 14)
        }
    }
}

private fun colors(transport: BuildingTransport) : BuildingElementsColors {
    return when (transport) {
        is BuildingElevator -> BuildingElementsColors.ELEVATOR
        is BuildingTeleport -> BuildingElementsColors.TELEPORT
        is BuildingShuttlePod -> BuildingElementsColors.SHUTTLE
        else -> BuildingElementsColors.ELEVATOR
    }
}