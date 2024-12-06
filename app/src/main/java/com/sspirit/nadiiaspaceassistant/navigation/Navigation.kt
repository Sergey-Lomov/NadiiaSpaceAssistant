package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.CosmonavigationTask
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.CosmonavigationTaskExecutionView
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
            route = Routes.CosmonavigationTaskByRequest.route + "/{requestJson}",
            arguments = listOf(navArgument("requestJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("requestJson")
            val request = Json.decodeFromString<CosmonavigationTaskGenerationRequest>(json ?: "")
            CosmonavigationTaskView(request, navController)
        }

        composable(
            route = Routes.CosmonavigationTaskByPOI.route + "/{indicesJson}",
            arguments = listOf(navArgument("indicesJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(json ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]

            // TODO: Add support for player current piloting skill
            val request = CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = 1.0f,
                stepDurationMultiplier = 1.0f,
                adaptiveDifficult = 1.0f
            )
            CosmonavigationTaskView(request, navController)
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

        composable(
            route = Routes.CosmonavigationTaskExecution.route + "/{time}",
            arguments = listOf(navArgument("time") { type = NavType.FloatType })
        ) { backStackEntry ->
            val time = backStackEntry.arguments?.getFloat("time") ?: 0f
            CosmonavigationTaskExecutionView(time, navController)
        }
    }
}