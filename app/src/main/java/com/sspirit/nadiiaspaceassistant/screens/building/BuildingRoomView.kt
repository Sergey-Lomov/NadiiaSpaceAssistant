package com.sspirit.nadiiaspaceassistant.screens.building

import android.icu.text.IDNA.Info
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.api.client.json.webtoken.JsonWebSignature.Header
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingMaterial
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassageway
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.LootGroupInstance
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.tablerows.LocationTableRowPassage
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.stringsToList
import org.w3c.dom.Entity

private val RoomSharedValue = compositionLocalOf<BuildingRoom?> { null }

@Composable
fun BuildingRoomView(missionId: String, locationId: String, realLocation: RealLifeLocation, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val room = mission.building.room(locationId, realLocation) ?: return

    val specLoot = room.specLoot.map { it.title }.toTypedArray()
    val devices = room.devices.map { it.string }.toTypedArray()
    val events = room.events.map { it.string }.toTypedArray()

    ScreenWrapper(navController, "${room.location.title} : ${realLocation.string}") {
        CompositionLocalProvider(RoomSharedValue provides room) {
            ScrollableColumn {
                InfoCard()
                EntityList("Лут", room.loot) { LootCard(it) }
                StringsList("Спец. лут", specLoot)
                StringsList("Устройства", devices)
                StringsList("События", events)
                EntityList("Проходы", room.passages) { PassageCard(it) }
                EntityList("Стены", room.walls) { WallCard(it) }
            }
        }
    }
}

@Composable
private fun InfoCard() {
    val room = RoomSharedValue.current ?: return

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            val address = "${room.location.title} -> ${room.location.title}(${room.location.id})"

            TitlesValuesList(mapOf(
                "Адресс" to address,
                "Положение" to room.realLocation.string,
                "Свет" to if (room.light) "Да" else "Нет"
            ))

            if (room.loot.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Лут", "${room.loot.size} : ${room.specLoot.size}")
            }

            if (room.specLoot.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Спец. лут", room.specLoot.size.toString())
            }

            if (room.devices.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Устройства", room.devices.size.toString())
            }

            if (room.events.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("События", room.events.size.toString())
            }
        }
    }
}

@Composable
private fun <T> EntityList(title: String, array: Array<T>, card: @Composable (T) -> Unit) {
    if (array.isNotEmpty()) {
        SpacedHorizontalDivider()
        HeaderText(title)
        Spacer(Modifier.height(8.dp))
        for (entity in array) {
            card(entity)
            if (entity !== array.last())
                Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LootCard(loot: LootGroupInstance) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            TitlesValuesList(mapOf(
                "Группа" to "${loot.lootGroup.title}(${loot.lootGroup.id})",
                "Предмет" to loot.item.title,
                "Кол-во" to loot.amount.toString(),
                "Цена" to "${loot.amount * loot.item.sellPrice}",
            ))
        }
    }
}

@Composable
private fun StringsList(title: String, strings: Array<String>) {
    if (strings.isNotEmpty()) {
        SpacedHorizontalDivider()
        HeaderText(title)
        Spacer(Modifier.height(8.dp))
        for (string in strings) {
            Card {
                RegularText(
                    text = string,
                    align = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
private fun MaterialPropertiesList(material: BuildingMaterial) {
    TitlesValuesList(mapOf(
        "Понятность материала" to material.lucidity.string,
        "Взрывоустойчивость" to if(material.explosionImmune) "Да" else "Нет",
        "Теплоустойчивость" to if(material.heatImmune) "Да" else "Нет",
        "Ксилотоустойчивость" to if(material.acidImmune) "Да" else "Нет",
    ))
}

@Composable
private fun PassageCard(passage: BuildingPassageway) {
    val room = RoomSharedValue.current ?: return
    val anotherRoom = if (passage.room1 == room)passage.room2 else passage.room1

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText("В ${anotherRoom.realLocation.string}")
            TitleValueRow("Тип", passage.type.string)

            val door = passage.door
            if (door != null) {
                SpacedHorizontalDivider(4)

                when (door.locks.size) {
                    0 -> TitleValueRow("Замок","Нет")
                    1 -> TitleValueRow("Замок", door.locks[0].readable())
                    else -> {
                        val locks = stringsToList(door.locks.map { it.readable() })
                        RegularText("Замки: \n$locks")
                    }
                }

                Spacer(Modifier.height(8.dp))
                TitlesValuesList(mapOf(
                    "Взлом" to door.hacking.string,
                    "Механизм" to door.turn.string,
                ))
                Spacer(Modifier.height(8.dp))
                MaterialPropertiesList(door.material)
            }

            val vent = passage.vent
            if (vent != null) {
                SpacedHorizontalDivider(4)
                TitlesValuesList(mapOf(
                    "Размер вент." to vent.size.string,
                    "Решетка вент." to vent.grilleState.string,
                ))
            }
        }
    }
}

@Composable
private fun WallCard(wall: BuildingWall) {
    val room = RoomSharedValue.current ?: return
    val anotherRoom = if (wall.room1 == room)wall.room2 else wall.room1

    Card {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            HeaderText("В ${anotherRoom.realLocation.string}")
            TitleValueRow("Дыра", if(wall.hasHole) "Да" else "Нет")
            MaterialPropertiesList(wall.material)
        }
    }
}