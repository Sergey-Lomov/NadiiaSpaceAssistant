package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.extensions.stringComposable
import com.sspirit.nadiiaspaceassistant.extensions.strings2Composable
import com.sspirit.nadiiaspaceassistant.extensions.strings3Composable
import com.sspirit.nadiiaspaceassistant.extensions.strings4Composable
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.generators.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskExecutionView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskRequestView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskView
import com.sspirit.nadiiaspaceassistant.screens.MainMenu
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingLocationView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingPassageView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingRoomView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSectorView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSlabView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingTransportView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectSelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOISelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemSelectionView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterRoutineView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOIDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOIPlaceDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestProposalView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestsDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionsListView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesExecutionView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesProposalView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestsExecutionView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingView
import com.sspirit.nadiiaspaceassistant.screens.building.ui.BuildingWallView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationAnalyzeView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationDetailsView
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

        composable(Routes.MissionsList.route) {
            MissionsListView(navController)
        }

        composable(Routes.MedsTestsProposal.route) {
            MedsTestProposalView(navController)
        }

        stringComposable(Routes.MedsTestsDetails) {
            MedsTestsDetailsView(it, navController)
        }

        stringComposable(Routes.MedsTestsExecution) {
            MedsTestsExecutionView(it, navController)
        }

        composable(Routes.EnergyLinesProposal.route) {
            EnergyLinesProposalView(navController)
        }

        stringComposable(Routes.EnergyLinesDetails) {
            EnergyLinesDetailsView(it, navController)
        }

        stringComposable(Routes.EnergyLinesExecution) {
            EnergyLinesExecutionView(it, navController)
        }

        stringComposable(Routes.PropertyEvacuationDetails) {
            PropertyEvacuationDetailsView(it, navController)
        }

        stringComposable(Routes.PropertyEvacuationAnalyzing) {
            PropertyEvacuationAnalyzeView(it, navController)
        }

        stringComposable(Routes.BuildingDetails) {
            BuildingView(it, navController)
        }

        strings2Composable(Routes.BuildingTransportDetails) { missionId, transportId ->
            BuildingTransportView(missionId, transportId, navController)
        }

        strings2Composable(Routes.BuildingSectorDetails) { missionId, index ->
            BuildingSectorView(missionId, index.toInt(), navController)
        }

        strings2Composable(Routes.BuildingLocationDetails) { missionId, locId ->
            BuildingLocationView(missionId, locId, navController)
        }

        strings3Composable(Routes.BuildingRoomDetails) { missionId, locId, realLocation ->
            val real = RealLifeLocation.byString(realLocation)
            BuildingRoomView(missionId, locId, real, navController)
        }

        strings3Composable(Routes.BuildingPassageDetails) { missionId, locId, indexString ->
            val index = indexString.toIntOrNull() ?: 0
            BuildingPassageView(missionId, locId, index, navController)
        }

        strings3Composable(Routes.BuildingWallDetails) { missionId, locId, indexString ->
            val index = indexString.toIntOrNull() ?: 0
            BuildingWallView(missionId, locId, index, navController)
        }

        strings4Composable(Routes.BuildingWallDetails) { missionId, sectorTitle, levelString, realLocation ->
            val level = levelString.toFloatOrNull() ?: 0.5f
            val real = RealLifeLocation.byString(realLocation)
            BuildingSlabView(missionId, sectorTitle, level, real, navController)
        }
    }
}