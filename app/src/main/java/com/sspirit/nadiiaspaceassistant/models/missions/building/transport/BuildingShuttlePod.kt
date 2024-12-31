package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

private const val TRAVEL_DURATION = 40

class BuildingShuttlePod(
    override val id: String,
    override val building: Building,
    override val rooms: Array<BuildingRoom>
) : BuildingTransport {
    override val title: String
        get() = "Монорельс"

    override fun timeCost(from: BuildingRoom, to: BuildingRoom): Int = TRAVEL_DURATION
}