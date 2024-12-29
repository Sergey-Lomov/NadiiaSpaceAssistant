package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.utils.stringComposable
import com.sspirit.nadiiaspaceassistant.utils.strings2Composable
import com.sspirit.nadiiaspaceassistant.utils.strings3Composable
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.models.missions.building.RealLifeLocation
import com.sspirit.nadiiaspaceassistant.screens.InfoDialogView
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
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingWallView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillCheckView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationAnalyzeView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.utils.stringsComposable
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

        stringComposable(Routes.InfoDialog) {
            InfoDialogView(it, navController)
        }

        composable(Routes.Cosmonavigation.route) {
            CosmonavigationMenu(navController)
        }

        stringComposable(Routes.CosmonavigationTaskByPOI) {
            val request = Json.decodeFromString<CosmonavigationTaskGenerationRequest>(it)
            CosmonavigationTaskView(request, navController)
        }

        stringComposable(Routes.CosmonavigationTaskByPOI) {
            val indices = Json.decodeFromString<Array<Int>>(it)
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

        stringComposable(Routes.SpaceSystemSelection) {
            val nextRoutes = Json.decodeFromString<Array<String>>(it)
            SpaceSystemSelectionView(nextRoutes, navController)
        }

        strings2Composable(Routes.SpaceObjectSelection) { indicesJson, routesJson ->
            val indices = Json.decodeFromString<Array<Int>>(indicesJson)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson)

            SpaceObjectSelectionView(system, nextRoutes, navController)
        }

        strings2Composable(Routes.SpacePOISelection) { indicesJson, routesJson ->
            val indices = Json.decodeFromString<Array<Int>>(indicesJson)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson)

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

        stringComposable(Routes.CharacterRoutine) {
            val type = CharacterSkillType.byId(it)
            CharacterRoutineView(type, navController)
        }

        strings3Composable(Routes.CharacterSkillCheck) { check, success, fail ->
            CharacterSkillCheckView(check, success, fail, navController)
        }

        stringComposable(Routes.SpaceSystemDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            SpaceSystemDetailsView(system, navController)
        }

        stringComposable(Routes.SpaceObjectDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            SpaceObjectDetailsView(spaceObject, navController)
        }

        stringComposable(Routes.SpacePOIDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            SpacePOIDetailsView(poi, navController)
        }

        stringComposable(Routes.SpacePOIPlaceDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
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

        stringsComposable(Routes.BuildingSlabDetails, 5) {
            val level = it[2].toFloatOrNull() ?: 0.5f
            val viewPointLevel = it[3].toIntOrNull()
            val real = RealLifeLocation.byString(it[4])
            BuildingSlabView(it[0], it[1], level, viewPointLevel, real, navController)
        }
    }
}