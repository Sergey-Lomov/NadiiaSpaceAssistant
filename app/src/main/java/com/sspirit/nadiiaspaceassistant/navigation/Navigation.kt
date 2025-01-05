package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sspirit.nadiiaspaceassistant.utils.stringComposable
import com.sspirit.nadiiaspaceassistant.utils.strings2Composable
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationRequest
import com.sspirit.nadiiaspaceassistant.services.fabrics.CosmonavigationTaskGenerationType
import com.sspirit.nadiiaspaceassistant.models.character.CharacterSkillType
import com.sspirit.nadiiaspaceassistant.screens.InfoDialogView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationMenu
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskExecutionView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskRequestView
import com.sspirit.nadiiaspaceassistant.screens.cosmonavigation.CosmonavigationTaskView
import com.sspirit.nadiiaspaceassistant.screens.MainMenu
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingBigObjectView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingLocationView
import com.sspirit.nadiiaspaceassistant.screens.building.loot.BuildingLootContainerView
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
import com.sspirit.nadiiaspaceassistant.screens.building.ItemSelectorView
import com.sspirit.nadiiaspaceassistant.screens.building.TransportRoomSelectionView
import com.sspirit.nadiiaspaceassistant.screens.building.devices.BuildingDeviceRouterView
import com.sspirit.nadiiaspaceassistant.screens.building.events.BuildingEventRouterView
import com.sspirit.nadiiaspaceassistant.screens.building.loot.BuildingLootContainerEditView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillCheckView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationAnalyzeView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationDetailsView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.utils.modelComposable
import com.sspirit.nadiiaspaceassistant.viewmodels.building.ItemSelectorViewModel
import kotlinx.serialization.json.Json

@Composable
fun Navigation(){
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = Routes.Main.route
    ) {
        composable(Routes.Main.route) {
            MainMenu(navigator)
        }

        modelComposable(Routes.InfoDialog) {
            InfoDialogView(it, navigator)
        }

        composable(Routes.Cosmonavigation.route) {
            CosmonavigationMenu(navigator)
        }

        stringComposable(Routes.CosmonavigationTaskByPOI) {
            val request = Json.decodeFromString<CosmonavigationTaskGenerationRequest>(it)
            CosmonavigationTaskView(request, navigator)
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
            CosmonavigationTaskView(request, navigator)
        }

        composable(Routes.CosmonavigationTaskRequest.route) {
            CosmonavigationTaskRequestView(navigator)
        }

        stringComposable(Routes.SpaceSystemSelection) {
            val nextRoutes = Json.decodeFromString<Array<String>>(it)
            SpaceSystemSelectionView(nextRoutes, navigator)
        }

        strings2Composable(Routes.SpaceObjectSelection) { indicesJson, routesJson ->
            val indices = Json.decodeFromString<Array<Int>>(indicesJson)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson)

            SpaceObjectSelectionView(system, nextRoutes, navigator)
        }

        strings2Composable(Routes.SpacePOISelection) { indicesJson, routesJson ->
            val indices = Json.decodeFromString<Array<Int>>(indicesJson)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson)

            SpacePOISelectionView(spaceObject, nextRoutes, navigator)
        }

        composable(
            route = Routes.CosmonavigationTaskExecution.route + "/{time}",
            arguments = listOf(navArgument("time") { type = NavType.FloatType })
        ) { backStackEntry ->
            val time = backStackEntry.arguments?.getFloat("time") ?: 0f
            CosmonavigationTaskExecutionView(time, navigator)
        }

        composable(Routes.CharacterSkills.route) {
            CharacterSkillsView(navigator)
        }

        stringComposable(Routes.CharacterRoutine) {
            val type = CharacterSkillType.byId(it)
            CharacterRoutineView(type, navigator)
        }

        modelComposable(Routes.CharacterSkillCheck) {
            CharacterSkillCheckView(it, navigator)
        }

        stringComposable(Routes.SpaceSystemDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            SpaceSystemDetailsView(system, navigator)
        }

        stringComposable(Routes.SpaceObjectDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            SpaceObjectDetailsView(spaceObject, navigator)
        }

        stringComposable(Routes.SpacePOIDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            SpacePOIDetailsView(poi, navigator)
        }

        stringComposable(Routes.SpacePOIPlaceDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.spaceMap[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            val place = poi.places[indices[3]]
            SpacePOIPlaceDetailsView(place, navigator)
        }

        composable(Routes.MissionsList.route) {
            MissionsListView(navigator)
        }

        composable(Routes.MedsTestsProposal.route) {
            MedsTestProposalView(navigator)
        }

        stringComposable(Routes.MedsTestsDetails) {
            MedsTestsDetailsView(it, navigator)
        }

        stringComposable(Routes.MedsTestsExecution) {
            MedsTestsExecutionView(it, navigator)
        }

        composable(Routes.EnergyLinesProposal.route) {
            EnergyLinesProposalView(navigator)
        }

        stringComposable(Routes.EnergyLinesDetails) {
            EnergyLinesDetailsView(it, navigator)
        }

        stringComposable(Routes.EnergyLinesExecution) {
            EnergyLinesExecutionView(it, navigator)
        }

        stringComposable(Routes.PropertyEvacuationDetails) {
            PropertyEvacuationDetailsView(it, navigator)
        }

        stringComposable(Routes.PropertyEvacuationAnalyzing) {
            PropertyEvacuationAnalyzeView(it, navigator)
        }

        modelComposable(Routes.ItemsSelector) {
            ItemSelectorView(it, navigator)
        }

        stringComposable(Routes.BuildingDetails) {
            BuildingView(it, navigator)
        }

        modelComposable(Routes.BuildingTransportDetails) {
            BuildingTransportView(it, navigator)
        }

        modelComposable(Routes.BuildingTransportRoomsSelection) {
            TransportRoomSelectionView(it, navigator)
        }

        modelComposable(Routes.BuildingSectorDetails) {
            BuildingSectorView(it, navigator)
        }

        modelComposable(Routes.BuildingLocationDetails) {
            BuildingLocationView(it, navigator)
        }

        modelComposable(Routes.BuildingRoomDetails) {
            BuildingRoomView(it, navigator)
        }

        modelComposable(Routes.BuildingPassageDetails) {
            BuildingPassageView(it, navigator)
        }

        modelComposable(Routes.BuildingWallDetails) {
            BuildingWallView(it, navigator)
        }

        modelComposable(Routes.BuildingSlabDetails) {
            BuildingSlabView(it, navigator)
        }

        modelComposable(Routes.BuildingBigObjectDetails) {
            BuildingBigObjectView(it, navigator)
        }

        modelComposable(Routes.BuildingDeviceDetails) {
            BuildingDeviceRouterView(it, navigator)
        }

        modelComposable(Routes.BuildingEventDetails) {
            BuildingEventRouterView(it, navigator)
        }

        modelComposable(Routes.BuildingLootContainerDetails) {
            BuildingLootContainerView(it, navigator)
        }

        modelComposable(Routes.BuildingLootContainerEdit) {
            BuildingLootContainerEditView(it, navigator)
        }
    }
}