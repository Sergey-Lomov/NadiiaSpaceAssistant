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
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObject
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingBigObjectPosition
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingPassageCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel

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
//                    Spacer(Modifier.height(8.dp))
//                    PushIntoHoleButton()
                }
            }
        }
    }
}

@Composable
private fun StatusText() {
    val obj = LocalObject.current ?: return
    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)

    if (!obj.isMovable(power)) {
        Spacer(Modifier.height(8.dp))
        RegularText(
            text = "Объект слишком тяжелый для игрока",
            color = colorResource(R.color.soft_red),
            autofill = true
        )
    }
}

@Composable
private fun InfoCard() {
    val obj = LocalObject.current ?: return

    Card {
        Column(Modifier.padding(16.dp)) {
            val coordinates = "${obj.room.location.sector.title} : ${obj.room.location.title} : ${obj.room.realLocation.string}"
            TitlesValuesList(mapOf(
                "Id" to obj.id,
                "Размер" to obj.size,
                "Координаты" to coordinates,
                "Положение" to obj.position.toString()
            ))
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
private fun ToCenterButton() {
    val missionId = LocalMissionId.current ?: return
    val obj = LocalObject.current ?: return
    val loadingState = LocalLoadingState.current ?: return

    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val movable = obj.isMovable(power)
    val isInCenter = obj.position == BuildingBigObjectPosition.Center

    AutosizeStyledButton(
        title = "В центр",
        enabled = movable && !isInCenter
    ) {
        TimeManager.handleBigObjectMoving()
        simpleCoroutineLaunch(loadingState) {
            val position = BuildingBigObjectPosition.Center
            DataProvider.updateBigObjectPosition(missionId, obj, position)
        }
    }
}

@Composable
private fun ChangePositionButton() {
    val missionId = LocalMissionId.current ?: return
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return

    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val movable = obj.isMovable(power)

    fun handleAction(state: MutableState<Boolean>, position: BuildingBigObjectPosition) {
        TimeManager.handleBigObjectMoving()
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

    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val movable = obj.isMovable(power)
    val rooms = obj.room.connectedRooms

    AutosizeStyledButton(
        title = "Передвинуть в комнату",
        enabled = movable && rooms.isNotEmpty()
    ) {
        TimeManager.handleBigObjectMoving()

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

    val power = CharacterDataProvider.character.progress(CharacterSkillType.POWER)
    val movable = obj.isMovable(power)
    val transports = obj.room.transports

//    AutosizeStyledButton(
//        title = "Перевезти на транспорте",
//        enabled = movable && transports.isNotEmpty()
//    ) {
//        TimeManager.handleBigObjectMoving()
//
//        val dialogModel = InfoDialogViewModel(
//            title = "Перемещение груза",
//            info = "Выберите транспорт для перемещения",
//        )
//
//        for (transport in transports) {
//            dialogModel.actions[transport.title] = { isLoading ->
//                coroutineLaunch(
//                    state = isLoading,
//                    task = {
//                        DataProvider.updateBigObjectRoom(missionId, obj, room)
//                    },
//                    completion = {
//                        navigator.navigateToRoom(missionId, obj.room)
//                    }
//                )
//            }
//        }
//
//        navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
//    }
}