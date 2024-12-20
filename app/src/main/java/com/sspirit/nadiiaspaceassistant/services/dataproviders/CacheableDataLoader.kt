package com.sspirit.nadiiaspaceassistant.services.dataproviders

import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MedsTestsDataProvider
import com.sspirit.nadiiaspaceassistant.services.dataproviders.missions.MissionsListDataProvider

object CacheableDataLoader {

    var loading: Boolean = false;

    fun reload() {
        loading = true;
        ItemDataProvider.getDescriptors()
        CharacterDataProvider.getCharacter()
        CosmologyDataProvider.getSpaceMap()
        MissionsListDataProvider.getMissions()
        MedsTestsDataProvider.downloadProgressions()
        loading = false;
    }
}