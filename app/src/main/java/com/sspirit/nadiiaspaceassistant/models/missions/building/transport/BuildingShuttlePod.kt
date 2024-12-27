package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

class BuildingShuttlePod(
    override val id: String,
    override val building: Building,
    override val rooms: Array<BuildingRoom>
) : BuildingTransport {
    override val title: String
        get() = "Монорельс"
}