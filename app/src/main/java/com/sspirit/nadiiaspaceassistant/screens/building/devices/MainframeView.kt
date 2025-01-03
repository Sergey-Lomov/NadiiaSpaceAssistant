package com.sspirit.nadiiaspaceassistant.screens.building.devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.models.missions.PropertyEvacuationGoal
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.models.missions.building.devices.BuildingDevice
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.DataProvider
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.utils.navigateWithModel
import com.sspirit.nadiiaspaceassistant.utils.simpleCoroutineLaunch
import com.sspirit.nadiiaspaceassistant.viewmodels.CharacterSkillCheckViewModel
import com.sspirit.nadiiaspaceassistant.viewmodels.InfoDialogViewModel

@Composable
fun MainframeView(missionId: String, device: BuildingDevice, navigator: NavHostController) {
    val mission = DataProvider.getBy(missionId) ?: return
    BuildingDeviceView(device, navigator) {
        val dataGoal = mission.goal == PropertyEvacuationGoal.DATA
        AutosizeStyledButton(
            title = "Искать данные задания",
            enabled = dataGoal
        ) {
            val check = SkillChecksManager.searchGoalData()
            val onFail = { TimeManager.mainframeGoalDataSearch() }
            val onSuccess = {
                TimeManager.mainframeGoalDataSearch()
                val dialogModel = InfoDialogViewModel(
                    title = "Найдены данные",
                    info = "Вы нашли данные, которые были целью задания"
                )
                dialogModel.actions["Принять"] = {
                    navigator.popBackStack()
                    navigator.popBackStack()
                }
                navigator.navigateWithModel(Routes.InfoDialog, dialogModel)
            }
            val checkModel = CharacterSkillCheckViewModel(check, onSuccess, onFail)
            navigator.navigateWithModel(Routes.CharacterSkillCheck, checkModel)
        }
    }
}