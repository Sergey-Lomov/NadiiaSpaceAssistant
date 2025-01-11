package com.sspirit.nadiiaspaceassistant.navigation

sealed class BuildingRoutes(val route: String) {
    data object InfoDialog : Routes("InfoDialog")

    data object CharacterSkillCheck : Routes("Character.Skill.Check")

    data object NoGroupItemsSelector : Routes("Items.NoGroupSelector")
    data object ItemsQuantumStorages : Routes("Items.QuantumStorages")
    data object ItemsQuantumStorageEdit : Routes("Items.QuantumStorage.Edit")
    data object ItemsQuantumStorageIdEdit : Routes("Items.QuantumStorage.Id.Edit")
    data object LootContainerItemsSelector : Routes("Items.LootContainerSelector")
    data object LootGroupSelector : Routes("LootGroup.Selector")

    data object Details : Routes("Building.Details")
    data object TransportDetails : Routes("Building.Transport.Details")
    data object TransportRoomsSelection : Routes("Building.Transport.RoomSelection")
    data object SectorDetails : Routes("Building.Sector.Details")
    data object LocationDetails : Routes("Building.Location.Details")
    data object RoomDetails : Routes("Building.Room.Details")
    data object PassageDetails : Routes("Building.Passage.Details")
    data object WallDetails : Routes("Building.Wall.Details")
    data object SlabDetails : Routes("Building.Slab.Details")
    data object BigObjectDetails : Routes("Building.BigObject.Details")
    data object DeviceDetails : Routes("Building.Device.Details")
    data object EventDetails : Routes("Building.Event.Details")
    data object LootContainerDetails : Routes("Building.LootContainer.Details")
    data object LootContainerEdit : Routes("Building.LootContainer.Edit")
}