package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun TransportRowWithCircles(transports: Array<BuildingTransport>, issue: String = "Нет транспорта") {
    if (transports.isNotEmpty())
        Row (modifier = Modifier.fillMaxWidth()) {
            RegularText("Транспорт", false)
            Spacer(Modifier.weight(1f))
            TransportCircles(transports)
        }
    else
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
            val colors = colors(transport)
            ColoredCircle(colors.back(), colors.info(), 25, transport.id)
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