package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.LootGroupInstance
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSlabCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable

private val LocalRoomValue = compositionLocalOf<BuildingRoom?> { null }
private val LocalNavigatorValue = compositionLocalOf<NavHostController?> { null }
private val LocalMissionIdValue = compositionLocalOf<String?> { null }

@Composable
fun BuildingRoomView(missionId: String, locationId: String, realLocation: RealLifeLocation, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.getBy(missionId) ?: return
    val room = mission.building.room(locationId, realLocation) ?: return

    val specLoot = room.specLoot.map { it.title }.toTypedArray()
    val devices = room.devices.map { it.string }.toTypedArray()
    val events = room.events.map { it.string }.toTypedArray()

    ScreenWrapper(navController, "${room.location.title} : ${realLocation.string}") {
        CompositionLocalProvider(
            LocalRoomValue provides room,
            LocalNavigatorValue provides navController,
            LocalMissionIdValue provides missionId
        ) {
            ScrollableColumn {
                InfoCard()
                EntityList("Лут", room.loot) { LootCard(it) }
                StringsList("Спец. лут", specLoot)
                StringsList("Устройства", devices)
                StringsList("События", events)
                EntityList("Транспорт", room.transports) { TransportCard(it) }
                EntityList("Большие объекты", room.bigObjects) { BuildingBigObjectCard(it) }
                EntityList("Проходы", room.passages) { PassageCard(it) }
                EntityList("Стены", room.walls) { WallCard(it) }
                EntityList("Перекрытия", room.slabs) { SlabCard(it) }
            }
        }
    }
}

@Composable
private fun InfoCard() {
    val room = LocalRoomValue.current ?: return

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            val address = "${room.location.title} -> ${room.location.title}(${room.location.id})"

            TitlesValuesList(mapOf(
                "Адресс" to address,
                "Положение" to room.realLocation.string,
                "Свет" to humanReadable(room.light)
            ))

            if (room.loot.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Лут", "${room.loot.size} : ${room.specLoot.size}")
            }

            if (room.specLoot.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                TitleValueRow("Спец. лут", room.specLoot.size.toString())
            }

            if (room.bigObjects.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                val bigObjectsString = room.bigObjects.joinToString("+") { it.size.toString() }
                TitleValueRow("Большие объекты", bigObjectsString)
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
private fun PassageCard(passage: BuildingPassage) {
    val room = LocalRoomValue.current ?: return
    val navigator = LocalNavigatorValue.current ?: return
    val missionId = LocalMissionIdValue.current ?: return
    val index = passage.location.passages.indexOf(passage)
    BuildingPassageCard(passage, room) {
        navigator.navigateTo(Routes.BuildingPassageDetails, missionId, room.location.id, index)
    }
}

@Composable
private fun WallCard(wall: BuildingWall) {
    val room = LocalRoomValue.current ?: return
    val navigator = LocalNavigatorValue.current ?: return
    val missionId = LocalMissionIdValue.current ?: return
    val index = wall.location.walls.indexOf(wall)
    BuildingWallCard(wall, room) {
        navigator.navigateTo(Routes.BuildingWallDetails, missionId, room.location.id, index)
    }
}

@Composable
private fun SlabCard(slab: BuildingSlab) {
    val room = LocalRoomValue.current ?: return
    val navigator = LocalNavigatorValue.current ?: return
    val missionId = LocalMissionIdValue.current ?: return
    BuildingSlabCard(slab, room) {
        navigator.navigateTo(
            Routes.BuildingSlabDetails,
            missionId,
            slab.sector.title,
            slab.level,
            room.location.level,
            slab.realLocation)
    }
}

@Composable
private fun TransportCard(transport: BuildingTransport) {
    val navigator = LocalNavigatorValue.current ?: return
    val missionId = LocalMissionIdValue.current ?: return
    BuildingTransportCard(transport) {
        navigator.navigateTo(Routes.BuildingTransportDetails, missionId ,transport.id)
    }
}

@Composable
fun BuildingBigObjectCard(obj: BuildingBigObject) {
    val navigator = LocalNavigatorValue.current ?: return
    val missionId = LocalMissionIdValue.current ?: return

    Card(
        onClick = {
            navigator.navigateTo(Routes.BuildingBigObjectDetails, missionId, obj.id)
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            TitlesValuesList(mapOf(
                "Id" to obj.id,
                "Размер" to obj.id,
                "Положение" to obj.fullPosition
            ))
        }
    }
}