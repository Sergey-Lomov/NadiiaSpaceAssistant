package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.runtime.Composable
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun BuildingVentRow(vent: BuildingVent) {
    val grillSign = when (vent.grilleState) {
        BuildingVentGrilleState.MISSED -> "✖"
        BuildingVentGrilleState.UP -> "⬆"
        BuildingVentGrilleState.DOWN -> "⬇"
        BuildingVentGrilleState.UNDEFINED -> "?"
    }
    TitleValueRow("Вентиляция", "${vent.size.string} $grillSign")
}