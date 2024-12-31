package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import kotlin.math.abs

private const val TRAVEL_PER_FLOOR_DURATION = 5

class BuildingElevator(
    override val id: String,
    override val building: Building,
    override val rooms: Array<BuildingRoom>
) : BuildingTransport {
    override val title: String
        get() = "Лифт"

    override fun timeCost(from: BuildingRoom, to: BuildingRoom): Int =
        TRAVEL_PER_FLOOR_DURATION * abs(from.location.level - to.location.level)
}