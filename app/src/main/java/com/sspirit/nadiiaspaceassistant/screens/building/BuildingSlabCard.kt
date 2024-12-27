package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.extensions.toString
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

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
            TitleValueRow("Дыра", if(slab.hasHole) "Да" else "Нет")
            BuildingMaterialRow(slab.material)
        }
    }
}