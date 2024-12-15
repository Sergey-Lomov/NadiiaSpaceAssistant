package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.services.dataproviders.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.dataproviders.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskExecutionView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskRequestView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskView
import com.sspirit.nadiiaspaceassistant.screens.MainMenu
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectSelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOISelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemSelectionView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterRoutineView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOIDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOIPlaceDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
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

            val adaptive = CharacterDataProvider.character.level(CharacterSkillType.PILOTING)
            val request = CosmonavigationTaskGenerationRequest(
                type = CosmonavigationTaskGenerationType.RANDOM,
                sequenceLengthMultiplier = poi.navigationLengthMultiplier,
                stepDurationMultiplier = poi.navigationTimeMultiplier,
                adaptiveDifficult = adaptive
            )
            CosmonavigationTaskView(request, navController)
        }

        composable(Routes.CosmonavigationTaskRequest.route) {
            CosmonavigationTaskRequestView(navController)
        }

        composable(
            route = Routes.SpaceSystemSelection.route + "/{nextRoutes}",
            arguments = listOf(navArgument("nextRoutes") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("nextRoutes")
            val nextRoutes = Json.decodeFromString<Array<String>>(json ?: "")
            SpaceSystemSelectionView(nextRoutes, navController)
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

        composable(Routes.CharacterSkills.route) {
            CharacterSkillsView(navController)
        }

        composable(
            route = Routes.CharacterRoutine.route + "/{skillId}",
            arguments = listOf(navArgument("skillId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("skillId") ?: ""
            val type = CharacterSkillType.byId(id)
            CharacterRoutineView(type, navController)
        }

        composable(
            route = Routes.SpaceSystemDetails.route + "/{indicesJson}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            SpaceSystemDetailsView(system, navController)
        }

        composable(
            route = Routes.SpaceObjectDetails.route + "/{indicesJson}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            SpaceObjectDetailsView(spaceObject, navController)
        }

        composable(
            route = Routes.SpacePOIDetails.route + "/{indicesJson}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            SpacePOIDetailsView(poi, navController)
        }

        composable(
            route = Routes.SpacePOIPlaceDetails.route + "/{indicesJson}",
            arguments = listOf(
                navArgument("indicesJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val indicesJson = backStackEntry.arguments?.getString("indicesJson")
            val indices = Json.decodeFromString<Array<Int>>(indicesJson ?: "")
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            val place = poi.places[indices[3]]
            SpacePOIPlaceDetailsView(place, navController)
        }
    }
}