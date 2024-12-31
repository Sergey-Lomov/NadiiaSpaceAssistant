package com.sspirit.nadiiaspaceassistant.screens.building

import android.provider.ContactsContract.Data
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportRoomCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingElementViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.building.RelativeBuildingElementViewModel

typealias BuildingTransportViewModel = RelativeBuildingElementViewModel<BuildingTransport>

@Composable
fun BuildingTransportView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingTransportViewModel>(modelId) ?: return
    val transport = model.element
    val viewPoint = model.viewPoint

    ScreenWrapper(navigator, transport.title + "(${transport.id})") {
        ScrollableColumn {
            for (room in transport.rooms) {
                BuildingTransportRoomCard(transport, room, viewPoint) {
                    if (viewPoint == null)
                        navigator.navigateToRoom(model.missionId, room)
                    else {
                        val duration = transport.timeCost(viewPoint, room)
                        val dialogModel = InfoDialogViewModel(
                            title = "Подтверждение перемещения",
                            info = "Перемещение на транспорте займет $duration сек",
                        )

                        dialogModel.actions["Перейти для просмотра"] = {
                            navigator.navigateToRoom(model.missionId, room)
                        }

                        dialogModel.actions["Испольозвать транспорт"] = { state ->
                            TimeManager.handlePlayerTransportation(transport, viewPoint, room)
                            coroutineLaunch(
                                state = state,
                                task = {
                                    for (obj in viewPoint.bigObjects)
                                        DataProvider.updateBigObjectRoom(model.missionId, obj, room)
                                },
                                completion = {
                                    navigator.navigateToRoom(model.missionId, room)
                                }
                            )
                        }

                        navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
                    }
                }
                if (room !== transport.rooms.last())
                    Spacer(Modifier.height(8.dp))
            }
        }
    }
}