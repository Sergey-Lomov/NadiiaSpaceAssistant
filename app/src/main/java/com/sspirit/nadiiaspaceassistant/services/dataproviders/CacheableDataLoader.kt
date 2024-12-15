package com.sspirit.nadiiaspaceassistant.services.dataproviders

object CacheableDataLoader {

    var loading: Boolean = false;

    fun reload() {
        loading = true;
        ItemDataProvider.getDescriptors(true)
        CharacterDataProvider.getCharacter(true)
        CosmologyDataProvider.getSpaceMap(true)
        loading = false;
    }
}