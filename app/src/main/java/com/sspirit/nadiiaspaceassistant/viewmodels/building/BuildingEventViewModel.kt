package com.sspirit.nadiiaspaceassistant.viewmodels.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice

data class BuildingEventViewModel (
    val missionId: String,
    val room: BuildingRoom,
    val event: BuildingEvent
)