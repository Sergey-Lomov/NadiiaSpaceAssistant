package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import kotlin.math.abs

@Composable
fun CeilFallEventView(isHard: Boolean, navigator: NavHostController) {
    val lowFail = "Вы не смогли поймать плиту, но обошлось без серьезных травм. Нужно только потратить ${TimeManager.ceilingFallFail} сек чтобы прийти в себя"
    val hardFail = "Вы не смогли поймать плиту, и она разбила вам голову! Эта серьезная травма снижает разум на 5, и не даст вам взламывать никакие замки кроме самых простых."
    val lowOnFail = { TimeManager.ceilingFallFail() }

    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Изношенные крепления не выдерживают и несколько плит потолочной отделки падает прямо на вас! Увернуться сразу от всех не выйдет, прийдется попробовать словить ту, что прямо над вами.",
        check = SkillChecksManager.ceilingFallEvent(),
        failDescription = if (isHard) hardFail else lowFail,
        successDescription = "Вы поймали плиту, и избежали неприятностей",
        onFail = { state ->
            if (!isHard) {
                lowOnFail()
                return@BuildingEventWithCheckView
            } else {
                simpleCoroutineLaunch(state) {
                    val trait = CharacterTraitsGenerator.oneDayHeadGash()
                    CharacterDataProvider.addTrait(trait)
                }
            }
        },
    )
}