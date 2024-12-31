package com.sspirit.nadiiaspaceassistant.viewmodels.building

import androidx.compose.runtime.MutableState
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.transport.BuildingTransport

data class TransportRoomSelectionViewModel (
    val missionId: String,
    val transport: BuildingTransport,
    val from: BuildingRoom,
    val onSelect: (BuildingRoom, MutableState<Boolean>) -> Unit
)