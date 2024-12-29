package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.models.missions.building.BuildingRoom
import com.sspirit.nadiiaspaceassistant.navigation.Routes
import com.sspirit.nadiiaspaceassistant.services.ValuesRegister
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.coroutineContext

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
    val id = ValuesRegister.register(model)
    navigateTo(route, id)
}

fun NavHostController.navigateToRoom(missionId: String, room: BuildingRoom) {
    val route = fullRoute(Routes.BuildingDetails, 1)
    popBackStack(route, false)

    val sector = room.location.sector
    val sectorIndex = sector.building.sectors.indexOf(sector)
    navigateTo(Routes.BuildingSectorDetails, missionId, sectorIndex)

    navigateTo(Routes.BuildingLocationDetails, missionId, room.location.id)
    navigateTo(Routes.BuildingRoomDetails, missionId, room.location.id, room.realLocation)
}

private fun fullRoute(route: Routes, paramsAmount: Int): String {
    val range = 1..paramsAmount
    val paramsString = range.fold("") {acc, i -> "$acc/{s$i}"}
    return route.route + paramsString
}