package com.sspirit.nadiiaspaceassistant.viewmodels.building

data class BuildingElementViewModel<T>(
    val missionId: String,
    val element: T
)