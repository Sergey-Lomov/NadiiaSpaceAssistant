package com.sspirit.nadiiaspaceassistant.navigation

sealed class Routes(val route: String) {
    data object Main : Routes("MainMenu")

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

    data object CharacterSkills : Routes("Character.Skills")
    data object CharacterRoutine : Routes("Character.Routine")

    data object MissionsList : Routes("Missions.List")

    data object MedsTestsProposal : Routes("MedsTests.Proposal")
    data object MedsTestsDetails : Routes("MedsTests.Details")
    data object MedsTestsExecution : Routes("MedsTests.Execution")

    data object EnergyLinesProposal : Routes("EnergyLines.Proposal")
    data object EnergyLinesDetails : Routes("EnergyLines.Details")
    data object EnergyLinesExecution : Routes("EnergyLines.Execution")

    data object PropertyEvacuationDetails : Routes("PropertyEvacuation.Details")
    data object PropertyEvacuationAnalyzing : Routes("PropertyEvacuation.Analyzing")

    data object BuildingDetails : Routes("Building.Details")
    data object BuildingTransportDetails : Routes("Building.Transport.Inspection")
    data object BuildingSectorDetails : Routes("Building.Sector.Inspection")
    data object BuildingLocationDetails : Routes("Building.Location.Inspection")
    data object BuildingRoomDetails : Routes("Building.Room.Inspection")
    data object BuildingPassageDetails : Routes("Building.Passage.Inspection")
    data object BuildingWallDetails : Routes("Building.Wall.Inspection")
    data object BuildingSlabDetails : Routes("Building.Slab.Inspection")
}