package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow

@Composable
fun BuildingWallCard(
    wall: BuildingWall,
    relativeRoom: BuildingRoom? = null,
    onClick: (() -> Unit)? = null
) {
    val header = if (relativeRoom != null) {
        val anotherRoom = if (wall.room1 == relativeRoom) wall.room2 else wall.room1
        "C ${anotherRoom.realLocation.string}"
    } else {
        "${wall.room1.realLocation.string} - ${wall.room2.realLocation.string}"
    }

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText(header)
            TitleValueRow("Дыра", if(wall.hasHole) "Да" else "Нет")
            BuildingMaterialRow(wall.material)
        }
    }
}