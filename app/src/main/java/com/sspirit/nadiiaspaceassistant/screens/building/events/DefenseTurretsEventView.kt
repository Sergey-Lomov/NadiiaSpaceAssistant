package com.sspirit.nadiiaspaceassistant.screens.building.events

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager
import com.sspirit.nadiiaspaceassistant.services.SkillChecksManager
import kotlin.math.abs

@Composable
fun DefenseTurretsEventView(navigator: NavHostController) {
    BuildingEventWithCheckView(
        navigator = navigator,
        description = "Неожиданно раздается механический лязг. В потолке открываются отверстия из которых выдвигаются автоматические защитные турели! Кажется пора уносить ноги!",
        check = SkillChecksManager.defendsTurretEvent(),
        failDescription = "Похоже вы выбежали из комнаты достаточно быстро - небыло слышно нидоного выстрела. Немного выждав (${TimeManager.defenseTurretsFail} сек) чтобы успокоиться, вы аккуратно заглядываете в комнату и замечаете около турелей красные огоньки - они отключены.",
        successDescription = "Ваш разум опережает тело! Уже поворачиваясь чтобы бежать, вы понимаете, что около турелей горят красные лампочки - они отключены. Нервы и время успешно сохранены!",
        onFail = { TimeManager.defenseTurretsFail() },
    )
}