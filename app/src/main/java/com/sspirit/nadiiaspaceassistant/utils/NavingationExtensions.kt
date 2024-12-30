package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingLocationViewModel
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingRoomViewModel
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSectorViewModel
import com.sspirit.nadiiaspaceassistant.services.ViewModelsRegister

fun NavGraphBuilder.stringComposable(route: Routes, builder: @Composable (String) -> Unit ) {
    stringsComposable(route, 1) { builder(it[0]) }
}

fun NavGraphBuilder.strings2Composable(route: Routes, builder: @Composable (String, String) -> Unit ) {
    stringsComposable(route, 2) { builder(it[0], it[1]) }
}

fun NavGraphBuilder.strings3Composable(route: Routes, builder: @Composable (String, String, String) -> Unit ) {
    stringsComposable(route, 3) { builder(it[0], it[1], it[2]) }
}

fun NavGraphBuilder.stringsComposable(route: Routes, amount: Int, builder: @Composable (Array<String>) -> Unit ) {
    val range = 1..amount
    composable(
        route = fullRoute(route, amount),
        arguments = range.map { navArgument("s$it") { type = NavType.StringType } }
    ) { entry ->
        val array = range
            .map {entry.arguments?.getString("s$it") ?: ""}
            .toTypedArray()
        builder(array)
    }
}

private val observedId: MutableSet<String> = mutableSetOf()
fun NavGraphBuilder.modelComposable(route: Routes, builder: @Composable (String) -> Unit ) {
    composable(
        route = route.route + "/{id}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType }
        )
    ) { entry ->
        val id = entry.arguments?.getString("id") ?: ""
        builder(id)

        if (id !in observedId) {
            observedId.add(id)
            entry.lifecycle.addObserver(
                object : DefaultLifecycleObserver {
                    override fun onDestroy(owner: LifecycleOwner) {
                        ViewModelsRegister.unregister(id)
                        observedId.remove(id)
                    }
                }
            )
        }
    }
}

fun NavHostController.navigateTo(route: Routes, vararg params: Any) {
    val paramsString = params.fold("") {acc, param -> "$acc/$param"}
    val isMainThread = Thread.currentThread().name == "main"
    if (isMainThread)
        navigate(route.route + paramsString)
    else
        mainLaunch {
            navigate(route.route + paramsString)
        }
}

fun NavHostController.navigateWithModel(route: Routes, model: Any) {
    val id = ViewModelsRegister.register(model)
    navigateTo(route, id)
}

fun NavHostController.navigateToRoom(missionId: String, room: BuildingRoom) {
    val route = fullRoute(Routes.BuildingDetails, 1)
    popBackStack(route, false)

    val sectorModel = BuildingSectorViewModel(missionId, room.location.sector)
    navigateWithModel(Routes.BuildingSectorDetails, sectorModel)

    val locationModel = BuildingLocationViewModel(missionId, room.location)
    navigateWithModel(Routes.BuildingLocationDetails, locationModel)
    val roomModel = BuildingRoomViewModel(missionId, room)
    navigateWithModel(Routes.BuildingRoomDetails, roomModel)
}

private fun fullRoute(route: Routes, paramsAmount: Int): String {
    val range = 1..paramsAmount
    val paramsString = range.fold("") {acc, i -> "$acc/{s$i}"}
    return route.route + paramsString
}