package uk.govuk.app.search.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchLocalDataSource @Inject constructor(
    private val realmProvider: SearchRealmProvider,
    private val topicsDataStore: SearchDataStore
) {

}