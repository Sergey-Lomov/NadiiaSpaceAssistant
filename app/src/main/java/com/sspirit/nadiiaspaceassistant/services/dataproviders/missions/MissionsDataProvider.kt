package com.sspirit.nadiiaspaceassistant.services.dataproviders.missions

interface MissionsDataProvider<T> {
    fun getBy(id: String): T?
    fun download(id: String)
}

interface MissionsProposalProvider<T> {
    fun getCurrentProposal(): T
    fun regenerateProposal()
    fun upload(mission: T)
}