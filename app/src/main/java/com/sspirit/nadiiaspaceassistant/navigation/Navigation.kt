package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.models.spacemap.SpaceObject
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationTaskRequestView
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationTaskView
import com.sspirit.nadiiaspaceassistant.screens.MainMenu
import com.sspirit.nadiiaspaceassistant.screens.SpaceObjectSelectionView
import com.sspirit.nadiiaspaceassistant.screens.SpacePOISelectionView
import com.sspirit.nadiiaspaceassistant.screens.StarSystemSelectionView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
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
            val nextRoutes = Json.decodeFromString<Array<String>>(json ?: "")
            StarSystemSelectionView(nextRoutes, navController)
        }

        composable(
            route = Routes.SpaceObjectSelection.route + "/{indicesJson}/{nextRoutes}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType },
                navArgument("nextRoutes") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]

            val routesJson = backStackEntry.arguments?.getString("nextRoutes")
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson ?: "")

            SpaceObjectSelectionView(system, nextRoutes, navController)
        }

        composable(
            route = Routes.SpacePOISelection.route + "/{indicesJson}/{nextRoutes}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType },
                navArgument("nextRoutes") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]

            val routesJson = backStackEntry.arguments?.getString("nextRoutes")
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson ?: "")

            SpacePOISelectionView(spaceObject, nextRoutes, navController)
        }
    }
}