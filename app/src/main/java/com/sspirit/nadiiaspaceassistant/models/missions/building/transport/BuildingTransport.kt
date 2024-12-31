package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

interface BuildingTransport {
    val id: String
    val building: Building
    val title: String
    val rooms: Array<BuildingRoom>

    fun timeCost(from: BuildingRoom, to: BuildingRoom): Int
}