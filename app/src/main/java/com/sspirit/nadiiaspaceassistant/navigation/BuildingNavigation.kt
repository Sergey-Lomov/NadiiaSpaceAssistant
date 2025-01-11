package com.sspirit.nadiiaspaceassistant.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.sspirit.nadiiaspaceassistant.utils.stringComposable
import com.sspirit.nadiiaspaceassistant.screens.InfoDialogView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingBigObjectView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingDetailsContentView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingLocationView
import com.sspirit.nadiiaspaceassistant.screens.building.loot.BuildingLootContainerView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingPassageView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingRoomView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSectorView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingSlabView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingTransportView
import com.sspirit.nadiiaspaceassistant.screens.building.BuildingWallView
import com.sspirit.nadiiaspaceassistant.screens.items.ItemSelectorView
import com.sspirit.nadiiaspaceassistant.screens.items.LootGroupSelectorView
import com.sspirit.nadiiaspaceassistant.screens.building.TransportRoomSelectionView
import com.sspirit.nadiiaspaceassistant.screens.building.devices.BuildingDeviceRouterView
import com.sspirit.nadiiaspaceassistant.screens.building.events.BuildingEventRouterView
import com.sspirit.nadiiaspaceassistant.screens.building.loot.BuildingLootContainerEditView
import com.sspirit.nadiiaspaceassistant.screens.building.loot.LootContainerItemsResolutions
import com.sspirit.nadiiaspaceassistant.screens.character.CharacterSkillCheckView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStorageEditView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStorageIdEditView
import com.sspirit.nadiiaspaceassistant.screens.items.QuantumStoragesView
import com.sspirit.nadiiaspaceassistant.utils.modelComposable

@Composable
fun BuildingNavigation(modelId: String) {
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = BuildingRoutes.Details.route + "/$modelId",
    ) {
        modelComposable(BuildingRoutes.InfoDialog) {
            InfoDialogView(it, navigator)
        }

        modelComposable(BuildingRoutes.NoGroupItemsSelector) {
            ItemSelectorView<Unit>(it, navigator)
        }

        modelComposable(BuildingRoutes.ItemsQuantumStorages) {
            QuantumStoragesView(it, navigator)
        }

        stringComposable(BuildingRoutes.ItemsQuantumStorageEdit) {
            QuantumStorageEditView(it, navigator)
        }

        modelComposable(BuildingRoutes.ItemsQuantumStorageIdEdit) {
            QuantumStorageIdEditView(it, navigator)
        }

        modelComposable(BuildingRoutes.CharacterSkillCheck) {
            CharacterSkillCheckView(it, navigator)
        }
        
        modelComposable(BuildingRoutes.LootContainerItemsSelector) {
            ItemSelectorView<LootContainerItemsResolutions>(it, navigator)
        }

        modelComposable(BuildingRoutes.LootGroupSelector) {
            LootGroupSelectorView(it, navigator)
        }

        stringComposable(BuildingRoutes.Details) {
            BuildingDetailsContentView(it, navigator)
        }

        modelComposable(BuildingRoutes.TransportDetails) {
            BuildingTransportView(it, navigator)
        }

        modelComposable(BuildingRoutes.TransportRoomsSelection) {
            TransportRoomSelectionView(it, navigator)
        }

        modelComposable(BuildingRoutes.SectorDetails) {
            BuildingSectorView(it, navigator)
        }

        modelComposable(BuildingRoutes.LocationDetails) {
            BuildingLocationView(it, navigator)
        }

        modelComposable(BuildingRoutes.RoomDetails) {
            BuildingRoomView(it, navigator)
        }

        modelComposable(BuildingRoutes.PassageDetails) {
            BuildingPassageView(it, navigator)
        }

        modelComposable(BuildingRoutes.WallDetails) {
            BuildingWallView(it, navigator)
        }

        modelComposable(BuildingRoutes.SlabDetails) {
            BuildingSlabView(it, navigator)
        }

        modelComposable(BuildingRoutes.BigObjectDetails) {
            BuildingBigObjectView(it, navigator)
        }

        modelComposable(BuildingRoutes.DeviceDetails) {
            BuildingDeviceRouterView(it, navigator)
        }

        modelComposable(BuildingRoutes.EventDetails) {
            BuildingEventRouterView(it, navigator)
        }

        modelComposable(BuildingRoutes.LootContainerDetails) {
            BuildingLootContainerView(it, navigator)
        }

        modelComposable(BuildingRoutes.LootContainerEdit) {
            BuildingLootContainerEditView(it, navigator)
        }
    }
}