package com.sspirit.nadiiaspaceassistant.screens.missions.energylines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.extensions.toString
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLines
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionStepControlPanel
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.EnergyLinesDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsPreviewsDataProvider
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.StyledButton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val checkRange = 6

@Composable
fun EnergyLinesExecutionView(id: String, navController: NavHostController) {
    val step = remember { mutableIntStateOf(0) }

    ScreenWrapper(navController) {
        val mission = EnergyLinesDataProvider.getBy(id) ?: return@ScreenWrapper

        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
        ) {
            when (step.intValue) {
                0 -> PreparationCard(mission)
                1 -> HeaderTextCard("Дорога", "Цель: ${mission.place}\n\nНе забудьте кабеля!")
                2 -> LandingStepView(mission, navController)
                3 -> HeaderTextCard("Получение данных", "Удалось получить следующие данные: ${mission.rules.joinToString("\n")}")
                4 -> HeaderTextCard("Ремонт", "Время выполнить ремонт.")
                5 -> HeaderTextCard("Награда", "- Игрок получает награду ${mission.reward}. Можно поторговаться за возмещение кабелей (следующий шаг).")
                6 -> {
                    val check = (MissionsPreviewsDataProvider.progressionDifficult / 1.75 + checkRange / 2.0).toInt()
                    HeaderTextCard(
                        "Торг",
                        "Не внезапная проверка Коммуникации: точность $checkRange, сложность $check"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            MissionStepControlPanel(step, 6, mission.id, navController)
        }
    }
}

@Composable
private fun LandingStepView(mission: EnergyLines, navController: NavHostController) {
    val message = "${mission.landingInfo}\n\nМод. длины: ${mission.landingLengthMult.toString(2)}\nМод. скорости: ${mission.landingTimeMult.toString(2)}"
    Column {
        HeaderTextCard("Маневрирование", message)
        Spacer(Modifier.height(16.dp))
        StyledButton(
            title = "Начать посадку",
            modifier = Modifier.fillMaxWidth()
        ) {
            val adaptive = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
            val request = CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = mission.landingLengthMult,
                stepDurationMultiplier = mission.landingTimeMult,
                adaptiveDifficult = adaptive
            )
            val json = Json.encodeToString(request)
            navController.navigate(Routes.CosmonavigationTaskByRequest.route + "/${json}")
        }
    }
}

@Composable
private fun PreparationCard(mission: EnergyLines) {
    var message = "Нужно расположить коннекторы."
    message += "\n\n" + if (mission.hardPlaces) "Используются сложные места. Нужно расположить лестицу." else "Только простые места"
    message += "\n\n" + if (mission.light) "Свет включен" else "Свет выключен"
    HeaderTextCard("Подготовка зоны", message)
}