package com.sspirit.nadiiaspaceassistant.screens.building

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitType
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.BuildingRoutes
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.TransportRoomSelectionViewModel

typealias BuildingBigObjectViewModel = BuildingElementViewModel<BuildingBigObject>

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalObject = compositionLocalOf<BuildingBigObject?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

@Composable
fun BuildingBigObjectView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingBigObjectViewModel>(modelId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, "Большой объект") {
        CompositionLocalProvider(
            LocalMissionId provides model.missionId,
            LocalObject provides model.element,
            LocalNavigator provides navigator,
            LocalLoadingState provides isLoading
        ) {
            LoadingOverlay(isLoading) {
                ScrollableColumn {
                    InfoCard()
                    StatusText()
                    RelatedElements()
                    SpacedHorizontalDivider()
                    ChangePositionButton()
                    Spacer(Modifier.height(8.dp))
                    MoveToRoomButton()
                    Spacer(Modifier.height(8.dp))
                    MoveByTransportButton()
                    Spacer(Modifier.height(8.dp))
                    PushIntoHoleButton()
                }
            }
        }
    }
}

@Composable
private fun StatusText() {
    val obj = LocalObject.current ?: return
    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val armBurn = CharacterDataProvider.character.hasTraitType(CharacterTraitType.ARM_ACID_BURN)

    if (!obj.isMovable(power)) {
        Spacer(Modifier.height(8.dp))
        CenteredRegularText(
            text = "Объект слишком тяжелый для игрока",
            color = colorResource(R.color.soft_red),
        )
    } else if (!obj.isMovable(Int.MAX_VALUE, armBurn)) {
        Spacer(Modifier.height(8.dp))
        CenteredRegularText(
            text = "Ожог руки не позволяет двигать предмет",
            color = colorResource(R.color.soft_red),
        )
    }
}

@Composable
private fun InfoCard() {
    val obj = LocalObject.current ?: return

    Card {
        Column(Modifier.padding(16.dp)) {
            val coordinates = "${obj.room.location.sector.title} : ${obj.room.location.title} : ${obj.room.realLocation.string}"
            TitlesValuesList(
                "Id" to obj.id,
                "Размер" to obj.size,
                "Координаты" to coordinates,
                "Положение" to obj.position.toString()
            )
        }
    }
}

@Composable
private fun RelatedElements() {
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return

    when (val position = obj.position) {
        BuildingBigObjectPosition.Free, BuildingBigObjectPosition.Undefined, BuildingBigObjectPosition.Center -> return
        is BuildingBigObjectPosition.NearWall -> {
            SpacedHorizontalDivider()
            BuildingWallCard(position.wall)
            Spacer(Modifier.height(8.dp))
            val relatedRoom = position.wall.anotherRoom(obj.room)
            BuildingRoomOverviewCard(relatedRoom) {
                navigator.navigateToRoom(missionId, relatedRoom)
            }
        }
        is BuildingBigObjectPosition.LockPassage -> {
            SpacedHorizontalDivider()
            BuildingPassageCard(position.passage)
            Spacer(Modifier.height(8.dp))
            val relatedRoom = position.passage.anotherRoom(obj.room)
            BuildingRoomOverviewCard(relatedRoom) {
                navigator.navigateToRoom(missionId, relatedRoom)
            }
        }
    }
}

@Composable
private fun ChangePositionButton() {
    val missionId = LocalMissionId.current ?: return
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return
    val movable = isObjectMovable(obj)

    fun handleAction(state: MutableState<Boolean>, position: BuildingBigObjectPosition) {
        TimeManager.bigObjectMoving()
        coroutineLaunch(
            state = state,
            task = { DataProvider.updateBigObjectPosition(missionId, obj, position) },
            completion = { navigator.popBackStack() }
        )
    }

    AutosizeStyledButton(
        title = "Двигать по комнате",
        enabled = movable
    ) {
        val dialogModel = InfoDialogViewModel(
            title = "Перемещение груза",
            info = "Выберите куда пододвинуть груз",
        )

        dialogModel.actions["Центр"] = { handleAction(it, BuildingBigObjectPosition.Center) }
        dialogModel.actions["Свободно"] = { handleAction(it, BuildingBigObjectPosition.Free) }

        for (wall in obj.room.walls) {
            val title = "Стена с " + wall.anotherRoom(obj.room).realLocation.string
            val position = BuildingBigObjectPosition.NearWall(wall)
            dialogModel.actions[title] = { handleAction(it, position) }
        }

        for (passage in obj.room.validPassages) {
            val title = "Проход в " + passage.anotherRoom(obj.room).realLocation.string
            val position = BuildingBigObjectPosition.LockPassage(passage)
            dialogModel.actions[title] = { handleAction(it, position) }
        }

        navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
    }
}

@Composable
private fun MoveToRoomButton() {
    val missionId = LocalMissionId.current ?: return
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return

    val movable = isObjectMovable(obj)
    val rooms = obj.room.connectedRooms

    AutosizeStyledButton(
        title = "Передвинуть в комнату",
        enabled = movable && rooms.isNotEmpty()
    ) {
        TimeManager.bigObjectMoving()

        val dialogModel = InfoDialogViewModel(
            title = "Перемещение груза",
            info = "Выберите комнату для перемещения",
        )

        for (room in rooms) {
            dialogModel.actions[room.realLocation.string] = { isLoading ->
                coroutineLaunch(
                    state = isLoading,
                    task = {
                        DataProvider.updateBigObjectRoom(missionId, obj, room)
                    },
                    completion = {
                        navigator.navigateToRoom(missionId, obj.room)
                    }
                )
            }
        }

        navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
    }
}

@Composable
private fun MoveByTransportButton() {
    val missionId = LocalMissionId.current ?: return
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return

    val movable = isObjectMovable(obj)
    val transports = obj.room.transports

    fun showRoomSelection(transport: BuildingTransport) {
        val model = TransportRoomSelectionViewModel(
            missionId = missionId,
            transport = transport,
            from = obj.room
        ) { newRoom, loadingState ->
            val oldRoom = obj.room
            coroutineLaunch(
                state = loadingState,
                task = { DataProvider.updateBigObjectRoom(missionId, obj, newRoom) },
                completion = {
                    if (obj.room != newRoom) return@coroutineLaunch
                    TimeManager.bigObjectTransportation(transport)
                    TimeManager.playerTransportation(transport, oldRoom, newRoom)
                    navigator.navigateToRoom(missionId, newRoom)
                }
            )
        }
        navigator.navigateWithModel(BuildingRoutes.TransportRoomsSelection, model)
    }

    AutosizeStyledButton(
        title = "Перевезти на транспорте",
        enabled = movable && transports.isNotEmpty()
    ) {
        if (transports.size == 1)
            showRoomSelection(transports.first())
        else {
            val dialogModel = InfoDialogViewModel(
                title = "Перемещение груза",
                info = "Выберите транспорт для перемещения",
            )

            for (transport in transports)
                dialogModel.actions[transport.title] = { showRoomSelection(transport) }
        }
    }
}

@Composable
fun PushIntoHoleButton() {
    val missionId = LocalMissionId.current ?: return
    val navigator = LocalNavigator.current ?: return
    val obj = LocalObject.current ?: return
    val loadingState = LocalLoadingState.current ?: return

    val movable = isObjectMovable(obj)
    val holeInFloor = obj.room.floor.hasHole
    val downRoom = obj.room.floor.downValidRoom

    AutosizeStyledButton(
        title = "Спихнуть в дыру",
        enabled = movable && holeInFloor && downRoom != null
    ) {
        if (downRoom == null) return@AutosizeStyledButton
        val currentRoom = obj.room
        TimeManager.bigObjectMoving()
        coroutineLaunch(
            state = loadingState,
            task = {
                val position = BuildingBigObjectPosition.Center
                DataProvider.updateBigObjectRoom(missionId, obj, downRoom, position)
            },
            completion = {
                if (obj.room == downRoom) {
                    navigator.navigateToRoom(missionId, currentRoom)
                }
            }
        )
    }
}

private fun isObjectMovable(obj: BuildingBigObject): Boolean {
    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val armBurn = CharacterDataProvider.character.hasTraitType(CharacterTraitType.ARM_ACID_BURN)
    return obj.isMovable(power, armBurn)
}