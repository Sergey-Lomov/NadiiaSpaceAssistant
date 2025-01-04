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
fun FloorFallEventView(navigator: NavHostController) {
    val model = LocalModel.current ?: return

    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Без надлежащго ухода даже самые надежные конструкции могут прийти в негодность. Пол прямо под вашими ногами проваливается и вы падаете вниз! Попытайтесь применить всю свою ловкость и избежать травм!",
        check = SkillChecksManager.floorFallEvent(),
        failDescription = "Вам не удалось приземлится достаточно умело - вы ушибли ногу. Ловкость снижается на 5. О прыжках с большой высоты пока-что лучше забыть.",
        successDescription = "Моментально сориентировавшись вы сгрупировались и мягко приземлились на ноги! Блестяще!",
        onFail = { state ->
            simpleCoroutineLaunch(state) {
                DataProvider.updateSlabHole(model.missionId, model.room.floor, true)
                val trait = CharacterTraitsGenerator.oneDayLegInjury()
                CharacterDataProvider.addTrait(trait)
            }
        },
        onSuccess = { state ->
            simpleCoroutineLaunch(state) {
                DataProvider.updateSlabHole(model.missionId, model.room.floor, true)
            }
        },
        customNavigation = {
            val bottomRoom = model.room.floor.downValidRoom ?: return@BuildingEventWithCheckView
            navigator.navigateToRoom(model.missionId, bottomRoom)
        }
    )
}