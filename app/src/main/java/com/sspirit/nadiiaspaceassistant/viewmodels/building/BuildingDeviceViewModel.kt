package com.sspirit.nadiiaspaceassistant.viewmodels.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

data class BuildingDeviceViewModel (
    val missionId: String,
    val room: BuildingRoom,
    val device: BuildingDevice
)