package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

interface MissionsDataProvider<T> {
    fun getCurrentProposal(): T
    fun getBy(id: String): T?
    fun regenerateProposal()
    fun upload(mission: T)
    fun download(id: String)
}