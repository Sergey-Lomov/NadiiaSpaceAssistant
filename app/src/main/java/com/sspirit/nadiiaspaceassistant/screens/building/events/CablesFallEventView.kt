package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import kotlin.math.abs

@Composable
fun CablesFallEventView(navigator: NavHostController) {
    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Потревоженные вашим присутствие, провода, идущие под потолком, падают прямо на вас! Попробуйте увернуться, если хватит ловкости!",
        check = SkillChecksManager.cablesFallEvent(),
        failDescription = "Вы не смогли увернуться от кабелей, и запутались в них. К счастью вас не ударило током, но теперь прийдется потратить ${abs(TimeManager.cablesFallFail)} сек чтобы освободиться.",
        successDescription = "Вы успели отскочить в сторону.",
        onFail = { TimeManager.cablesFallFail() },
    )
}