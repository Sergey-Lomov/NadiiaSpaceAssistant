package com.sspirit.nadiiaspaceassistant.navigation

sealed class Routes(val route: String) {
    data object Main : Routes("MainMenu")
    data object InfoDialog : Routes("InfoDialog")

    data object ItemsMenu : Routes("Items.Menu")
    data object NoGroupItemsSelector : Routes("Items.NoGroupSelector")
    data object ItemsQuantumStorages : Routes("Items.QuantumStorages")
    data object ItemsQuantumStorageEdit : Routes("Items.QuantumStorage.Edit")
    data object ItemsQuantumStorageIdEdit : Routes("Items.QuantumStorage.Id.Edit")

    data object SpaceSystemSelection : Routes("StarSystemSelection")
    data object SpaceObjectSelection : Routes("SpaceObjectSelection")
    data object SpacePOISelection : Routes("SpacePOISelection")

    data object Cosmonavigation : Routes("Cosmonavigation.Menu")
    data object CosmonavigationTaskByRequest : Routes("Cosmonavigation.Task.ByRequest")
    data object CosmonavigationTaskByPOI : Routes("Cosmonavigation.Task.ByPOI")
    data object CosmonavigationTaskExecution : Routes("Cosmonavigation.Task.Execution")
    data object CosmonavigationTaskRequest : Routes("Cosmonavigation.Request")

    data object SpaceSystemDetails : Routes("Cosmology.SystemDetails")
    data object SpaceObjectDetails : Routes("Cosmology.ObjectDetails")
    data object SpacePOIDetails : Routes("Cosmology.POIDetails")
    data object SpacePOIPlaceDetails : Routes("Cosmology.POIPlaceDetails")

    data object CharacterMenu : Routes("Character.Menu")
    data object CharacterSkills : Routes("Character.Skills")
    data object CharacterTraits : Routes("Character.Traits")
    data object CharacterRoutine : Routes("Character.Routine")
    data object CharacterSkillDetails : Routes("Character.Skill.Details")
    data object CharacterSkillCheck : Routes("Character.Skill.Check")
    data object CharacterDrugs : Routes("Character.Drugs")

    data object MissionsList : Routes("Missions.List")

    data object MedsTestsProposal : Routes("MedsTests.Proposal")
    data object MedsTestsDetails : Routes("MedsTests.Details")
    data object MedsTestsExecution : Routes("MedsTests.Execution")

    data object EnergyLinesProposal : Routes("EnergyLines.Proposal")
    data object EnergyLinesDetails : Routes("EnergyLines.Details")
    data object EnergyLinesExecution : Routes("EnergyLines.Execution")

    data object PropertyEvacuationDetails : Routes("PropertyEvacuation.Details")
    data object PropertyEvacuationAnalyzing : Routes("PropertyEvacuation.Analyzing")
    data object PropertyEvacuationExecution : Routes("PropertyEvacuation.Execution")
    data object PropertyEvacuationDashboard : Routes("PropertyEvacuation.Dashboard")

    data object BuildingDetailsView : Routes("Building.Details")
}