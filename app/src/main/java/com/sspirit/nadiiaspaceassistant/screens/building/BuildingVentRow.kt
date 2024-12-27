package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingVentGrilleState
import com.sspirit.nadiiaspaceassistant.ui.ColoredCircle
import com.sspirit.nadiiaspaceassistant.ui.RegularText
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