package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.EnergyNodeState
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CharacterTraitsGenerator
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun EngineerEpiphanyEventView(navigator: NavHostController) {
    val model = LocalModel.current ?: return
    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Вы неожиданно начинаете ощущать упорядоченность в расположении выключателей, розеток и ламп. Картина вокруг начинает складываться в единую систему.",
        check = SkillChecksManager.engineerEpiphanyEvent(),
        failDescription = "Странное ощущение приближающегося понимания исчезло так же внезапно как и появилось",
        successDescription = "Сосредоточившись, вы понимаете где находятся спрятанные в стенах кабеля и куда они должны вести. Сняв соответствующую панель, вы убеждаетесь в своей правоте. Вы обнаружили один из скрытых энергоузлов!",
        onSuccess = { state ->
            simpleCoroutineLaunch(state) {
                val node = BuildingDevice.EnergyNode(EnergyNodeState.UNOPTIMIZED)
                DataProvider.addDevice(model.missionId, model.room, node)
            }
        },
    )
}