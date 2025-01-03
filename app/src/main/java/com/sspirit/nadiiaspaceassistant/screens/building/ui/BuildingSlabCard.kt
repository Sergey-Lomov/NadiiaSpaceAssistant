package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable

@Composable
fun BuildingSlabCard(
    slab: BuildingSlab,
    relativeRoom: BuildingRoom? = null,
    onClick: (() -> Unit)? = null
) {
    val header = if (relativeRoom != null) {
        if (relativeRoom.ceiling == slab) "Потолок" else "Пол"
    } else {
        "${slab.sector.title} : ${slab.level.toString(1)} : ${slab.realLocation}"
    }

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText(header)
            TitleValueRow("Дыра", humanReadable(slab.hasHole))
            BuildingMaterialRow(slab.material)
        }
    }
}