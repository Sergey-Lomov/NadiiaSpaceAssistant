package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

interface BuildingTransport {
    val id: String
    val title: String
    val rooms: Array<BuildingRoom>
}