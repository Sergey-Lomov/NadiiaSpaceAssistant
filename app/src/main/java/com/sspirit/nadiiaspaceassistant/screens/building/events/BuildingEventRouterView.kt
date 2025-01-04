package com.sspirit.nadiiaspaceassistant.screens.building.events

import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingEvent
import com.sspirit.nadiiaspaceassistant.viewmodels.building.BuildingEventViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister


internal val LocalModel = compositionLocalOf<BuildingEventViewModel?> { null }

@Composable
fun BuildingEventRouterView(modelId: String, navigator: NavHostController) {
    val model = ViewModelsRegister.get<BuildingEventViewModel>(modelId) ?: return

    CompositionLocalProvider(LocalModel provides model) {
        when (model.event) {
            BuildingEvent.CABLES_FALL -> CablesFallEventView(navigator)
            BuildingEvent.CEIL_FALL -> CeilFallEventView(false, navigator)
            BuildingEvent.HARD_CEIL_FALL -> CeilFallEventView(true, navigator)
            BuildingEvent.FLOOR_FALL -> FloorFallEventView(navigator)
            BuildingEvent.DEFENSE_TURRET -> DefenseTurretsEventView(navigator)
            BuildingEvent.POISON_GAS -> PoisonGasEventView(navigator)
            BuildingEvent.PANIC_ATTACK -> PanicAttackEventView(navigator)
            BuildingEvent.ACID_CONTAINER -> AcidContainerEventView(navigator)
            BuildingEvent.ENGINEER_EPIPHANY -> EngineerEpiphanyEventView(navigator)
            BuildingEvent.UNDEFINED -> TODO()
        }
    }
}