package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.utils.navigateToRoom
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import kotlin.math.abs

@Composable
fun PoisonGasEventView(navigator: NavHostController) {
    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Из-за отсутствия вентиляции в комнате скопилось немного токсичных газов. Сможет ли ваше тело совладать с отравой?",
        check = SkillChecksManager.poisonGasEvent(),
        failDescription = "Ваше тело оказалось недостаточно подготовлено. У вас началась легкая интоксикация, которая наверняка пройдет когда вы завершите миссию и вернетесь в свежую атмосферу вашего корабля. До тех пор выши разум, ловкость и сила снижены на 4.",
        successDescription = "Ваше тело не только храм, но и крепость! Небольшая доза токсинов не в состоянии вам навредить!",
        onFail = { state ->
            simpleCoroutineLaunch(state) {
                val trait = CharacterTraitsGenerator.todayMildIntoxication()
                CharacterDataProvider.addTrait(trait)
            }
        },
    )
}