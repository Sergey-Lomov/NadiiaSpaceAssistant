package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationTaskRequestView
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationTaskView
import com.sspirit.nadiiaspaceassistant.screens.MainMenu
import com.sspirit.nadiiaspaceassistant.screens.SpaceObjectSelectionView
import com.sspirit.nadiiaspaceassistant.screens.StarSystemSelectionView
import kotlinx.serialization.json.Json

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Main.route
    ) {
        composable(Routes.Main.route) {
            MainMenu(navController)
        }

        composable(Routes.Cosmonavigation.route) {
            CosmonavigationMenu(navController)
        }

        composable(
            route = Routes.CosmonavigationTask.route + "/{taskJson}",
            arguments = listOf(navArgument("taskJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("taskJson")
            val task = Json.decodeFromString<CosmonavigationTask>(json ?: "")
            CosmonavigationTaskView(task, navController)
        }

        composable(Routes.CosmonavigationTaskRequest.route) {
            CosmonavigationTaskRequestView(navController)
        }

        composable(
            route = Routes.StarSystemSelection.route + "/{nextRoutes}",
            arguments = listOf(navArgument("nextRoutes") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("nextRoutes")
            var nextRoutes = arrayOf(Routes.Main.route)
            if (json != null) {
               nextRoutes = Json.decodeFromString<Array<String>>(json ?: "")
            }
            StarSystemSelectionView(nextRoutes, navController)
        }

        composable(
            route = Routes.SpaceObjectSelection.route + "/{starId}/{nextRoutes}",
            arguments = listOf(
                navArgument("starId") { type = NavType.StringType },
                navArgument("nextRoutes") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val starId = backStackEntry.arguments?.getString("starId")
            val json = backStackEntry.arguments?.getString("nextRoutes")
            var nextRoutes = arrayOf<String>()
            if (json != null) {
                nextRoutes = Json.decodeFromString<Array<String>>(json ?: "")
            }
            SpaceObjectSelectionView(starId, nextRoutes, navController)
        }
    }
}