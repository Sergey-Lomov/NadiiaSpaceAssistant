package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitTag
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch

@Composable
fun AutoDoctorView(
    device: BuildingDevice,
    navigator: NavHostController
) {
    val healable = arrayOf(CharacterTraitTag.TRAUMA, CharacterTraitTag.MALAISE)

    BuildingDeviceView(device, navigator) { state ->
        HeaderText("Можно вылечить")
        CenteredRegularText("Каждое лечение требует 2 энергии от топливных гранул и занимает 30 секунд")
        Spacer(Modifier.height(16.dp))

        val traits = healable.flatArrayMap {
            CharacterDataProvider.character.traitsByTag(it)
        }
        for (trait in traits) {
            AutosizeStyledButton(trait.type.title) {
                simpleCoroutineLaunch(state) {
                    CharacterDataProvider.removeTrait(trait) {
                        if (it) TimeManager.autoDoctorHealing()
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}