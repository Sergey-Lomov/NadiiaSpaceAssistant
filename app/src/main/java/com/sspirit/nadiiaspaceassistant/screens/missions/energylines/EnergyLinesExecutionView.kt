package com.sspirit.nadiiaspaceassistant.screens.missions.energylines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.utils.navigateTo
import com.sspirit.nadiiaspaceassistant.utils.toString
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.EnergyLines
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionStepControlPanel
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.EnergyLinesDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.ui.AutosizeStyledButton
import com.sspirit.nadiiaspaceassistant.ui.HeaderTextCard
import com.sspirit.nadiiaspaceassistant.ui.ScreenWrapper
import com.sspirit.nadiiaspaceassistant.ui.ScrollableColumn
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val checkRange = 6

@Composable
fun EnergyLinesExecutionView(id: String, navigator: NavHostController) {
    val step = remember { mutableIntStateOf(0) }

    ScreenWrapper(navigator) {
        val mission = EnergyLinesDataProvider.getBy(id) ?: return@ScreenWrapper

        ScrollableColumn {
            when (step.intValue) {
                0 -> PreparationCard(mission)
                1 -> HeaderTextCard("Дорога", "Цель: ${mission.place}\n\nНе забудьте кабеля!")
                2 -> LandingStepView(mission, navigator)
                3 -> HeaderTextCard("Получение данных", "Удалось получить следующие данные: ${mission.rules.joinToString("\n")}")
                4 -> HeaderTextCard("Ремонт", "Время выполнить ремонт.")
                5 -> HeaderTextCard("Награда", "- Игрок получает награду ${mission.reward}. Можно поторговаться за возмещение кабелей (следующий шаг).")
                6 -> {
                    val check = (MissionsListDataProvider.progressionDifficult + checkRange / 2.0).toInt()
                    HeaderTextCard(
                        "Торг",
                        "Не внезапная проверка Коммуникации: точность $checkRange, сложность $check"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            MissionStepControlPanel(step, 6, mission.id, navigator)
        }
    }
}

@Composable
private fun LandingStepView(mission: EnergyLines, navigator: NavHostController) {
    val message = "${mission.landingInfo}\n\nМод. длины: ${mission.landingLengthMult.toString(2)}\nМод. скорости: ${mission.landingTimeMult.toString(2)}"
    Column {
        HeaderTextCard("Маневрирование", message)
        Spacer(Modifier.height(16.dp))
        AutosizeStyledButton("Начать посадку") {
            val adaptive = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
            val request = CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = mission.landingLengthMult,
                stepDurationMultiplier = mission.landingTimeMult,
                adaptiveDifficult = adaptive
            )
            val json = Json.encodeToString(request)
            navigator.navigateTo(Routes.CosmonavigationTaskByRequest, json)
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