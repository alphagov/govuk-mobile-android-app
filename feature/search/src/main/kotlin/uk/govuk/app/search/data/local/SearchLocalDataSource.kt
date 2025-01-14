package uk.govuk.app.search.data.local

import io.realm.kotlin.ext.query
import uk.govuk.app.search.data.local.model.LocalSearchItem
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchLocalDataSource @Inject constructor(
    private val realmProvider: SearchRealmProvider,
) {

    suspend fun fetchPreviousSearches(): List<LocalSearchItem> {
        return realmProvider.open().query<LocalSearchItem>().find()
    }

    suspend fun insertOrUpdate(searchTerm: String) {
        realmProvider.open().writeBlocking {
            val localSearch = query<LocalSearchItem>("searchTerm = $0", searchTerm).first().find()
            val now = Calendar.getInstance().timeInMillis

            localSearch?.apply {
                this.timestamp = now
            } ?: copyToRealm(
                LocalSearchItem().apply {
                    this.searchTerm = searchTerm
                    this.timestamp = now
                }
            )
        }
    }
}