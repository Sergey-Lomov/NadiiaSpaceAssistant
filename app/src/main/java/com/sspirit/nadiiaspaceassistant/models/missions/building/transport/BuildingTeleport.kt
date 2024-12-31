package com.sspirit.nadiiaspaceassistant.models.missions.building.transport

import com.sspirit.nadiiaspaceassistant.models.missions.building.Building
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

private const val TRAVEL_DURATION = 0

class BuildingTeleport(
    override val id: String,
    override val building: Building,
    val room1: BuildingRoom,
    val room2: BuildingRoom,
) : BuildingTransport {
    override val title: String
        get() = "Телепорт"

    override val rooms: Array<BuildingRoom>
        get() = arrayOf(room1, room2)

    override fun timeCost(from: BuildingRoom, to: BuildingRoom): Int = TRAVEL_DURATION
}