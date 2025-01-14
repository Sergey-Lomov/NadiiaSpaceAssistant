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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingElevator
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingTransportRoomCard
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.LoadingOverlay
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import com.sspirit.nadiiaspaceassistant.ui.TitlesValuesList
import com.sspirit.nadiiaspaceassistant.viewmodels.building.TransportRoomSelectionViewModel

private val LocalModel = compositionLocalOf<TransportRoomSelectionViewModel?> { null }
private val LocalLoadingState = compositionLocalOf<MutableState<Boolean>?> { null }

@Composable
fun TransportRoomSelectionView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<TransportRoomSelectionViewModel>(modelId) ?: return
    val rooms = model.transport.rooms
    val isLoading = remember { mutableStateOf(false) }

    ScreenWrapper(navigator, model.transport.title, isLoading) {
        CompositionLocalProvider(
            LocalLoadingState provides isLoading,
            LocalModel provides model,
        ) {
            ScrollableColumn {
                for (room in rooms) {
                    if (room == model.from) continue
                    BuildingTransportRoomCard(model.transport, room, model.from) {
                        model.onSelect(room, isLoading)
                    }
                    if (room !== rooms.last())
                        Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}