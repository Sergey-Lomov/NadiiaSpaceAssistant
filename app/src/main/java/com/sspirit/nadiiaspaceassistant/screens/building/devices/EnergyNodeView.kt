package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.R
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingLocation
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.EnergyNodeState
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.CenteredRegularText
import com.sspirit.nadiiaspaceassistant.ui.RegularText
import com.sspirit.nadiiaspaceassistant.utils.mainLaunch
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel

@Composable
fun EnergyNodeView(
    missionId: String,
    room: BuildingRoom,
    node: BuildingDevice.EnergyNode,
    navigator: NavHostController
) {
    BuildingDeviceView(
        device = node,
        info = mapOf("Состояние" to { node.state }),
        navigator = navigator
    ) { state ->
        TimeManager
        when(node.state) {
            EnergyNodeState.UNOPTIMIZED ->
                OptimizationProposal(missionId, room.location, node, state, navigator)
            EnergyNodeState.OPTIMIZED ->
                CenteredRegularText(
                    text = "Узел уже оптимизирован: +${TimeManager.nodeOptimizationBonus} сек",
                    color = colorResource(R.color.soft_green)
                )
            EnergyNodeState.BROKEN ->
                CenteredRegularText(
                    text = "Узел поврежден: оптимизация невозможна",
                    color = colorResource(R.color.soft_red)
                )
            EnergyNodeState.UNDEFINED ->
                CenteredRegularText(
                    text = "Состояние узла неопределено",
                    color = Color.Red
                )
        }
    }
}

@Composable
fun OptimizationProposal(
    missionId: String,
    location: BuildingLocation,
    node: BuildingDevice.EnergyNode,
    state: MutableState<Boolean>,
    navigator: NavHostController
) {
    RegularText("Можно попробовать оптимизировать узел. В случае удачи получится задержать перегрузку реактора на ${TimeManager.nodeOptimizationBonus} сек. Но в случае неудачи узел будет поврежден и повторная попытка оптимизации станет невозможна.")
    Spacer(Modifier.height(8.dp))
    AutosizeStyledButton("Попробовать оптимизировать узел") {
        val onSuccess = {
            mainLaunch { navigator.popBackStack() }
            simpleCoroutineLaunch (state) {
                DataProvider.updateEnergyNodeState(missionId, location, node, EnergyNodeState.OPTIMIZED) {
                    if (it) TimeManager.energyNodeOptimization(true)
                }
            }
        }

        val onFail = {
            mainLaunch { navigator.popBackStack() }
            simpleCoroutineLaunch (state) {
                DataProvider.updateEnergyNodeState(missionId, location, node, EnergyNodeState.BROKEN) {
                    if (it) TimeManager.energyNodeOptimization(false)
                }
            }
        }

        val check = SkillChecksManager.optimizeEnergyNode()
        val viewModel = CharacterSkillCheckViewModel(check, onSuccess, onFail)
        navigator.navigateWithModel(Routes.CharacterSkillCheck, viewModel)
    }
}