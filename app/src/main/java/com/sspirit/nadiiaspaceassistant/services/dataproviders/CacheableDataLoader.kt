package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.RoomsDescriptorsDataProvider

object CacheableDataLoader {

    var loading: Boolean = false

    fun reloadMain() {
        loading = true
        ItemDataProvider.downloadDescriptors()
        CharacterDataProvider.downloadCharacter()
        CosmologyDataProvider.downloadSpaceMap()
        MissionsListDataProvider.downloadMissions()
        MedsTestsDataProvider.downloadProgressions()
        QuantumStorageDataProvider.downloadStorages()
        loading = false
    }

    fun reloadPropertyEvacuationData() {
        loading = true
        LootGroupsDataProvider.downloadLootGroups()
        RoomsDescriptorsDataProvider.downloadRoomsDescriptors()
        loading = false
    }
}