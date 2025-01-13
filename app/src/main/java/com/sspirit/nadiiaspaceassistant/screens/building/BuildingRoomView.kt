package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingPassage
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingSlab
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingWall
import com.sspirit.nadiiaspaceassistant.models.missions.building.specloot.BuildingSpecialLootContainer
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.loot.BuildingLootContainerViewModel
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingLootContainerCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingSlabCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.LootGroupsDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LocalSWLoadingState
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledIconButton
import com.sspirit.nadiiaspaceassistant.ui.TitleValueRow
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.ui.utils.humanReadable
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingDeviceViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingEventViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.LootGroupSelectorViewModel
import java.util.UUID

typealias BuildingRoomViewModel = BuildingElementViewModel<BuildingRoom>

private val LocalRoom = compositionLocalOf<BuildingRoom?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalMissionId = compositionLocalOf<String?> { null }

@Composable
fun BuildingRoomView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingRoomViewModel>(modelId) ?: return
    val room = model.element
    val isLoading = rememberSaveable { mutableStateOf(false) }
    val screenTitle = "${room.location.title} : ${room.realLocation.string}"

    ScreenWrapper(navigator, screenTitle, isLoading) {
        CompositionLocalProvider(
            LocalRoom provides room,
            LocalNavigator provides navigator,
            LocalMissionId provides model.missionId
        ) {
            ScrollableColumn {
                InfoCard()
                EntityList("Лут", room.loot) { LootCard(it) }
                EntityList("Спец. лут", room.specLoot) { SpecialLootCard(it) }
                EntityList("События", room.events) { EventCard(it) }
                EntityList("Устройства", room.devices) { DeviceCard(it) }
                EntityList("Транспорт", room.transports) { TransportCard(it) }
                EntityList("Большие объекты", room.bigObjects) { BuildingBigObjectCard(it) }
                EntityList("Проходы", room.passages) { PassageCard(it) }
                EntityList("Стены", room.walls) { WallCard(it) }
                EntityList("Перекрытия", room.slabs) { SlabCard(it) }
                SpacedHorizontalDivider()
                AddLootButton(navigator)
            }
        }
    }
}

@Composable
private fun InfoCard() {
    val room = LocalRoom.current ?: return

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            val address = "${room.location.sector.title} -> ${room.location.title}(${room.location.id})"

            TitlesValuesList(
                "Тип" to room.type,
                "Адресс" to address,
                "Положение" to room.realLocation.string,
                "Свет" to humanReadable(room.light)
            )

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

    if (!room.isValid) {
        Spacer(Modifier.height(8.dp))
        CenteredRegularText("Комната не валидна", colorResource(R.color.soft_red))
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
private fun LootCard(loot: BuildingLootContainer) {
    val missionId = LocalMissionId.current ?: return
    val navigator = LocalNavigator.current ?: return

    BuildingLootContainerCard(loot) {
        val model = BuildingLootContainerViewModel(missionId, loot)
        navigator.navigateWithModel(BuildingRoutes.LootContainerDetails, model)
    }
}

@Composable
private fun SpecialLootCard(container: BuildingSpecialLootContainer) {
    val missionId = LocalMissionId.current ?: return
    val loadingState = LocalSWLoadingState.current ?: return

    Card {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxSize().padding(end = 88.dp)
                ) {
                    RegularText(container.loot.title)
                }
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxSize()
                ) {
                    StyledIconButton(
                        icon = Icons.Default.BackHand,
                        description = "Pick up",
                        modifier = Modifier.width(80.dp)
                    ) {
                        simpleCoroutineLaunch(loadingState) {
                            DataProvider.pickUpSpecialLoot(missionId, container)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PassageCard(passage: BuildingPassage) {
    val room = LocalRoom.current ?: return
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    BuildingPassageCard(passage, room) {
        val model= BuildingPassageViewModel(missionId, passage, room)
        navigator.navigateWithModel(BuildingRoutes.PassageDetails, model)
    }
}

@Composable
private fun WallCard(wall: BuildingWall) {
    val room = LocalRoom.current ?: return
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    BuildingWallCard(wall, room) {
        val model = BuildingWallViewModel(missionId, wall, room)
        navigator.navigateWithModel(BuildingRoutes.WallDetails, model)
    }
}

@Composable
private fun SlabCard(slab: BuildingSlab) {
    val room = LocalRoom.current ?: return
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    BuildingSlabCard(slab, room) {
        val model = BuildingSlabViewModel(missionId, slab, room)
        navigator.navigateWithModel(BuildingRoutes.SlabDetails,model)
    }
}

@Composable
private fun TransportCard(transport: BuildingTransport) {
    val navigator = LocalNavigator.current ?: return
    val room = LocalRoom.current ?: return
    val missionId = LocalMissionId.current ?: return
    BuildingTransportCard(transport) {
        val model = BuildingTransportViewModel(missionId, transport, room)
        navigator.navigateWithModel(BuildingRoutes.TransportDetails, model)
    }
}

@Composable
fun BuildingBigObjectCard(obj: BuildingBigObject) {
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return

    Card(
        onClick = {
            val model = BuildingBigObjectViewModel(missionId, obj)
            navigator.navigateWithModel(BuildingRoutes.BigObjectDetails, model)
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            TitlesValuesList(
                "Id" to obj.id,
                "Размер" to obj.size,
                "Положение" to obj.fullPosition
            )
        }
    }
}

@Composable
fun DeviceCard(device: BuildingDevice) {
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    val room = LocalRoom.current ?: return

    Card(
        onClick = {
            val model = BuildingDeviceViewModel(missionId, room, device)
            navigator.navigateWithModel(BuildingRoutes.DeviceDetails, model)
        }
    ) {
        CenteredRegularText(device.title, modifier = Modifier.padding(8.dp))
    }
}

@Composable
fun EventCard(event: BuildingEvent) {
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return
    val room = LocalRoom.current ?: return

    Card(
        onClick = {
            val model = BuildingEventViewModel(missionId, room, event)
            navigator.navigateWithModel(BuildingRoutes.EventDetails, model)
        }
    ) {
        Column(Modifier.padding(16.dp)) {
            CenteredRegularText(event.title, modifier = Modifier.padding(8.dp))
            Spacer(Modifier.height(8.dp))
            TitleValueRow("Триггер", event.trigger)
        }
    }
}

@Composable
private fun AddLootButton(navigator: NavHostController) {
    val missionId = LocalMissionId.current ?: return
    val room = LocalRoom.current ?: return

    AutosizeStyledButton("Добавить лут") {
        val selectorModel = LootGroupSelectorViewModel(room) { group ->
            mainLaunch {
                val container = BuildingLootContainer(
                    id = UUID.randomUUID().toString(),
                    room = room,
                    group = group
                )
                val editorModel = BuildingLootContainerViewModel(missionId, container)
                navigator.navigateWithModel(BuildingRoutes.LootContainerEdit, editorModel)
            }
        }
        navigator.navigateWithModel(BuildingRoutes.LootGroupSelector, selectorModel)

    }
}