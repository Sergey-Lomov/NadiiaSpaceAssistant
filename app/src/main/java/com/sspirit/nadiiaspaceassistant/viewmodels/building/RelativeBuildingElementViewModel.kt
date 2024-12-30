package com.sspirit.nadiiaspaceassistant.viewmodels.building

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom

data class RelativeBuildingElementViewModel<T> (
    val missionId: String,
    val element: T,
    val viewPoint: BuildingRoom?
)