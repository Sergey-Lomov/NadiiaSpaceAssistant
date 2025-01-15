package com.sspirit.nadiiaspaceassistant.navigation

import com.sspirit.nadiiaspaceassistant.screens.building.TimeManager

object NavigationHandler {
    val onCreate: MutableMap<String, () -> Unit> = mutableMapOf()
    val onDestroy: MutableMap<String, () -> Unit> = mutableMapOf()

    fun handleCreation(route: String) = onCreate[pureRoute(route)]?.invoke()
    fun handleDestroying(route: String) = onDestroy[pureRoute(route)]?.invoke()

    fun setup() {
        onCreate[Routes.CharacterSkillCheck.route] = { TimeManager.pause() }
        onDestroy[Routes.CharacterSkillCheck.route] = { TimeManager.play() }
    }

    private fun pureRoute(route: String) = route.substringBefore("/")
}