package com.sspirit.nadiiaspaceassistant.utils

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.navigation.Routes

fun NavGraphBuilder.stringComposable(route: Routes, builder: @Composable (String) -> Unit ) {
    stringsComposable(route, 1) { builder(it[0]) }
}

fun NavGraphBuilder.strings2Composable(route: Routes, builder: @Composable (String, String) -> Unit ) {
    stringsComposable(route, 2) { builder(it[0], it[1]) }
}

fun NavGraphBuilder.strings3Composable(route: Routes, builder: @Composable (String, String, String) -> Unit ) {
    stringsComposable(route, 3) { builder(it[0], it[1], it[2]) }
}

fun NavGraphBuilder.strings4Composable(route: Routes, builder: @Composable (String, String, String, String) -> Unit ) {
    stringsComposable(route, 4) { builder(it[0], it[1], it[2], it[3]) }
}

fun NavGraphBuilder.stringsComposable(route: Routes, amount: Int, builder: @Composable (Array<String>) -> Unit ) {
    val range = 1..amount
    val paramsString = range.fold("") {acc, i -> "$acc/{s$i}"}
    composable(
        route = route.route + paramsString,
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
    navigate(route.route + paramsString)
}
//
//fun NavHostController.navigateTo(route: Routes, param: Any) {
//    navigate(route.route + "/$param")
//}
//
//fun NavHostController.navigateTo(route: Routes, param1: Any, param2: Any) {
//    navigate(route.route + "/$param1/$param2")
//}
//
//fun NavHostController.navigateTo(route: Routes, param1: Any, param2: Any, param3: Any) {
//    navigate(route.route + "/$param1/$param2/$param3")
//}
//
//fun NavHostController.navigateTo(route: Routes, param1: Any, param2: Any, param3: Any, param4: Any) {
//    navigate(route.route + "/$param1/$param2/$param3/$param4")
//}