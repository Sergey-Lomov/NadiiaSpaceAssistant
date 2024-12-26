package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.propertyevacuation.RoomsDescriptorsDataProvider

object CacheableDataLoader {

    var loading: Boolean = false;

    fun reloadMain() {
        loading = true;
        ItemDataProvider.getDescriptors()
        CharacterDataProvider.getCharacter()
        CosmologyDataProvider.getSpaceMap()
        MissionsListDataProvider.getMissions()
        MedsTestsDataProvider.downloadProgressions()
        loading = false;
    }

    fun reloadPropertyEvacuationData() {
        loading = true;
        LootGroupsDataProvider.getLootGroups()
        RoomsDescriptorsDataProvider.getRoomsLoot()
        loading = false;
    }
}