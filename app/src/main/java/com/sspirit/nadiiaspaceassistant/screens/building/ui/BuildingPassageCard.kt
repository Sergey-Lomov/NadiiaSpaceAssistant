package com.sspirit.nadiiaspaceassistant.screens.building.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList

@Composable
fun BuildingPassageCard(
    passage: BuildingPassage,
    relativeRoom: BuildingRoom? = null,
    onClick: (() -> Unit)? = null
) {
    val header = if (relativeRoom != null) {
        val anotherRoom = if (passage.room1 == relativeRoom) passage.room2 else passage.room1
        "В ${anotherRoom.realLocation.string}"
    } else {
        "${passage.room1.realLocation.string} - ${passage.room2.realLocation.string}"
    }

    Card(
        onClick = { onClick?.invoke() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText(header)
            TitleValueRow("Тип", passage.type.string)

            val door = passage.door
            if (door != null) {
                SpacedHorizontalDivider(4)

                when (door.locks.size) {
                    0 -> TitleValueRow("Замок","Нет")
                    1 -> TitleValueRow("Замок", door.locks[0].toString())
                    else -> {
                        val locks = stringsToList(door.locks.map { it.toString() })
                        RegularText("Замки: \n$locks")
                    }
                }

                if (door.locks.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    TitleValueRow("Взлом",door.hacking.string)
                }
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Механизм",door.turn.string)

                Spacer(Modifier.height(8.dp))
                BuildingMaterialRow(door.material)
            }

            val vent = passage.vent
            if (vent != null) {
                SpacedHorizontalDivider(4)
                BuildingVentRow(vent)
            }
        }
    }
}