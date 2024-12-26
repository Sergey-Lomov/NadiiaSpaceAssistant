package com.sspirit.nadiiaspaceassistant.extensions

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.navigation.Routes

fun NavGraphBuilder.stringComposable(route: Routes, builder: @Composable (String) -> Unit ) {
    composable(
        route = route.route + "/{s1}",
        arguments = listOf(navArgument("s1") { type = NavType.StringType })
    ) { entry ->
        val s1 = entry.arguments?.getString("s1") ?: ""
        builder(s1)
    }
}

fun NavGraphBuilder.strings2Composable(route: Routes, builder: @Composable (String, String) -> Unit ) {
    composable(
        route = route.route + "/{s1}/{s2}",
        arguments = listOf(
            navArgument("s1") { type = NavType.StringType },
            navArgument("s2") { type = NavType.StringType }
        )
    ) { entry ->
        val s1 = entry.arguments?.getString("s1") ?: ""
        val s2 = entry.arguments?.getString("s2") ?: ""
        builder(s1, s2)
    }
}

fun NavGraphBuilder.strings3Composable(route: Routes, builder: @Composable (String, String, String) -> Unit ) {
    composable(
        route = route.route + "/{s1}/{s2}/{s3}",
        arguments = listOf(
            navArgument("s1") { type = NavType.StringType },
            navArgument("s2") { type = NavType.StringType },
            navArgument("s3") { type = NavType.StringType }
        )
    ) { entry ->
        val s1 = entry.arguments?.getString("s1") ?: ""
        val s2 = entry.arguments?.getString("s2") ?: ""
        val s3 = entry.arguments?.getString("s3") ?: ""
        builder(s1, s2, s3)
    }
}

fun NavHostController.navigateTo(route: Routes) {
    navigate(route.route)
}

fun NavHostController.navigateTo(route: Routes, param: Any) {
    navigate(route.route + "/$param")
}

fun NavHostController.navigateTo(route: Routes, param1: Any, param2: Any) {
    navigate(route.route + "/$param1/$param2")
}

fun NavHostController.navigateTo(route: Routes, param1: Any, param2: Any, param3: Any) {
    navigate(route.route + "/$param1/$param2/$param3")
}