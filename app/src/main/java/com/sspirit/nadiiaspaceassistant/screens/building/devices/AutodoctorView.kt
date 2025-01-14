package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTrait
import com.sspirit.nadiiaspaceassistant.models.character.CharacterTraitTag
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.ElementsList
import com.sspirit.nadiiaspaceassistant.ui.HeaderText
import com.sspirit.nadiiaspaceassistant.ui.SpacedHorizontalDivider
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import com.sspirit.nadiiaspaceassistant.utils.coroutineLaunch
import com.sspirit.nadiiaspaceassistant.utils.flatArrayMap
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import kotlin.math.max

private const val REQUIRED_ENERGY = 2

@Composable
fun AutoDoctorView(
    missionId: String,
    room: BuildingRoom,
    device: BuildingDevice.AutoDoctor,
    navigator: NavHostController
) {
    BuildingDeviceView(
        device = device,
        navigator = navigator,
        info = mapOf("Энергия" to { device.energy }),
    ) { state ->
        HeaderText("Можно вылечить")
        CenteredRegularText("Каждое лечение требует 2⚡ от топливных гранул и занимает 30 секунд")
        Spacer(Modifier.height(16.dp))
        StatusText(device)
        EnergyPanel(missionId, room, device, state)
        TraitsList(missionId, room, device, state)
    }
}

@Composable
private fun StatusText(doctor: BuildingDevice.AutoDoctor) {
    if (doctor.energy >= REQUIRED_ENERGY) return
    CenteredRegularText("Недостаточно энергии", colorResource(R.color.soft_red))
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun TraitsList(
    missionId: String,
    room: BuildingRoom,
    doctor: BuildingDevice.AutoDoctor,
    loadingState: MutableState<Boolean>
) {
    val healable = arrayOf(CharacterTraitTag.TRAUMA, CharacterTraitTag.MALAISE)
    val traits = healable.flatArrayMap { CharacterDataProvider.character.traitsByTag(it) }
    if (traits.isEmpty()) return
    SpacedHorizontalDivider()
    ElementsList(traits) { TraitButton(it, missionId, room, doctor, loadingState) }
}

@Composable
private fun EnergyPanel(
    missionId: String,
    room: BuildingRoom,
    doctor: BuildingDevice.AutoDoctor,
    loadingState: MutableState<Boolean>
) {
    Row {
        ElementsList(1..4, vertical = false) {
            StyledButton(
                title = "+$it",
                modifier = Modifier.weight(1f)
            ) {
                simpleCoroutineLaunch (loadingState) {
                    val energy = doctor.energy + it
                    DataProvider.updateAutoDoctorEnergy(missionId, room.location, doctor, energy)
                }
            }
        }
    }
}

@Composable
private fun TraitButton(
    trait: CharacterTrait,
    missionId: String,
    room: BuildingRoom,
    doctor: BuildingDevice.AutoDoctor,
    loadingState: MutableState<Boolean>
) {
    AutosizeStyledButton(
        title = trait.type.title,
        enabled = doctor.energy >= REQUIRED_ENERGY
    ) {
        simpleCoroutineLaunch(loadingState) {
            CharacterDataProvider.removeTrait(trait) { removed ->
                if (removed) {
                    val energy = max(doctor.energy - 2, 0)
                    DataProvider.updateAutoDoctorEnergy(missionId, room.location, doctor, energy) { success ->
                        if (success) TimeManager.autoDoctorHealing()
                    }
                }
            }
        }
    }
}