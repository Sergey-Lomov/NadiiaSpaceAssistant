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
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingDetailsView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterMenu
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectSelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOISelectionView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemSelectionView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterRoutineView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceObjectDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpacePOIDetailsView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.places.SpacePOIPlaceRouterView
import com.sspirit.nadiiaspaceassistant.screens.cosmology.SpaceSystemDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestProposalView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestsDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.MissionsListView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesExecutionView
import com.sspirit.nadiiaspaceassistant.screens.missions.energylines.EnergyLinesProposalView
import com.sspirit.nadiiaspaceassistant.screens.missions.medstests.MedsTestsExecutionView
import com.sspirit.nadiiaspaceassistant.screens.items.ItemSelectorView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillCheckView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillDetailsView
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterTraitsView
import com.sspirit.nadiiaspaceassistant.screens.character.DrugsView
import com.sspirit.nadiiaspaceassistant.screens.items.ItemsMenuView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStorageEditView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStorageIdEditView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStoragesView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationAnalyzeView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationDashboardView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationDetailsView
import com.sspirit.nadiiaspaceassistant.screens.missions.proprtyevacuation.PropertyEvacuationExecutionView
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CharacterDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.CosmologyDataProvider
import com.sspirit.nadiiaspaceassistant.utils.modelComposable
import kotlinx.serialization.json.Json

@Composable
fun Navigation(startDestination: String = Routes.Main.route) {
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = startDestination
    ) {
        composable(Routes.Main.route) {
            MainMenu(navigator)
        }

        modelComposable(Routes.InfoDialog) {
            InfoDialogView(it, navigator)
        }

        composable(Routes.ItemsMenu.route) {
            ItemsMenuView(navigator)
        }

        modelComposable(Routes.NoGroupItemsSelector) {
            ItemSelectorView<Unit>(it, navigator)
        }

        modelComposable(Routes.ItemsQuantumStorages) {
            QuantumStoragesView(it, navigator)
        }

        stringComposable(Routes.ItemsQuantumStorageEdit) {
            QuantumStorageEditView(it, navigator)
        }

        modelComposable(Routes.ItemsQuantumStorageIdEdit) {
            QuantumStorageIdEditView(it, navigator)
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
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
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

        stringComposable(Routes.CosmonavigationTaskByRequest) {
            val request = Json.decodeFromString<CosmonavigationTaskGenerationRequest>(it)
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
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
            val nextRoutes = Json.decodeFromString<Array<String>>(routesJson)

            SpaceObjectSelectionView(system, nextRoutes, navigator)
        }

        strings2Composable(Routes.SpacePOISelection) { indicesJson, routesJson ->
            val indices = Json.decodeFromString<Array<Int>>(indicesJson)
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
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

        composable(Routes.CharacterMenu.route) {
            CharacterMenu(navigator)
        }

        composable(Routes.CharacterSkills.route) {
            CharacterSkillsView(navigator)
        }

        composable(Routes.CharacterTraits.route) {
            CharacterTraitsView(navigator)
        }

        stringComposable(Routes.CharacterRoutine) {
            val type = CharacterSkillType.byId(it)
            CharacterRoutineView(type, navigator)
        }

        composable(Routes.CharacterDrugs.route) {
            DrugsView(navigator)
        }

        stringComposable(Routes.CharacterSkillDetails) {
            CharacterSkillDetailsView(it, navigator)
        }

        modelComposable(Routes.CharacterSkillCheck) {
            CharacterSkillCheckView(it, navigator)
        }

        stringComposable(Routes.SpaceSystemDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
            SpaceSystemDetailsView(system, navigator)
        }

        stringComposable(Routes.SpaceObjectDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
            val spaceObject = system.objects[indices[1]]
            SpaceObjectDetailsView(spaceObject, navigator)
        }

        stringComposable(Routes.SpacePOIDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            SpacePOIDetailsView(poi, navigator)
        }

        stringComposable(Routes.SpacePOIPlaceDetails) {
            val indices = Json.decodeFromString<Array<Int>>(it)
            val system = CosmologyDataProvider.sectorMap.systems[indices[0]]
            val spaceObject = system.objects[indices[1]]
            val poi = spaceObject.pois[indices[2]]
            val place = poi.places[indices[3]]
            SpacePOIPlaceRouterView(place, navigator)
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

        stringComposable(Routes.PropertyEvacuationExecution) {
            PropertyEvacuationExecutionView(it, navigator)
        }

        modelComposable(Routes.PropertyEvacuationDashboard) {
            PropertyEvacuationDashboardView(it, navigator)
        }

        stringComposable(Routes.BuildingDetailsView) {
            BuildingDetailsView(it, navigator)
        }
    }
}