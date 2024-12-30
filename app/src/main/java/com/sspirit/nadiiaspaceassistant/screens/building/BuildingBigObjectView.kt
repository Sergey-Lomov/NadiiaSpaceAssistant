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
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingRoomOverviewCard
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.PropertyEvacuationDataProvider
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
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

private val LocalMissionId = compositionLocalOf<String?> { null }
private val LocalObject = compositionLocalOf<BuildingBigObject?> { null }
private val LocalNavigator = compositionLocalOf<NavHostController?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

@Composable
fun BuildingBigObjectView(missionId: String, objectId: String, navController: NavHostController) {
    val mission = PropertyEvacuationDataProvider.missions[missionId] ?: return
    val obj = mission.building.bigObject(objectId) ?: return
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navController, "Большой объект") {
        CompositionLocalProvider(
            LocalMissionId provides missionId,
            LocalObject provides obj,
            LocalNavigator provides navController,
            LocalLoadingState provides isLoading
        ) {
            LoadingOverlay(isLoading) {
                ScrollableColumn {
                    InfoCard()
                    StatusText()
                    RelatedRoom()
                    SpacedHorizontalDivider()
//                    ChangePosiotionButton()
                    Spacer(Modifier.height(8.dp))
                    MoveToRoomButton()
//                    Spacer(Modifier.height(8.dp))
//                    MoByTransportButton()
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
        RegularText("Объект слишком тяжелый для игрока", colorResource(R.color.soft_red))
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
                "Размер" to obj.id,
                "Координаты" to coordinates,
                "Положение" to obj.position.toString()
            ))
        }
    }
}

@Composable
private fun RelatedRoom() {
    val obj = LocalObject.current ?: return
    val navigator = LocalNavigator.current ?: return
    val missionId = LocalMissionId.current ?: return

    val relatedRoom = when (obj.position) {
        BuildingBigObjectPosition.Center -> null
        BuildingBigObjectPosition.Free -> null
        BuildingBigObjectPosition.Undefined -> null
        is BuildingBigObjectPosition.LockPassage -> {
            val passage = obj.position.passage
            if (passage.room1 == obj.room) passage.room2 else passage.room1
        }
        is BuildingBigObjectPosition.NearWall -> {
            val wall = obj.position.wall
            if (wall.room1 == obj.room) wall.room2 else wall.room1
        }
    }
    if (relatedRoom == null) return

    SpacedHorizontalDivider()
    BuildingRoomOverviewCard(relatedRoom) {
        navigator.navigateToRoom(missionId, relatedRoom)
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
                        PropertyEvacuationDataProvider.updateBigObjectRoom(missionId, obj, room)
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