package uk.govuk.app.search.data.local

import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import uk.govuk.app.search.data.local.model.LocalSearchItem
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchLocalDataSource @Inject constructor(
    private val realmProvider: SearchRealmProvider,
) {

    suspend fun fetchPreviousSearches(): List<LocalSearchItem> {
        return realmProvider.open()
            .query<LocalSearchItem>()
            .sort("timestamp", Sort.DESCENDING)
            .find()
    }

    suspend fun insertOrUpdatePreviousSearch(searchTerm: String) {
        realmProvider.open().writeBlocking {
            val localSearch = query<LocalSearchItem>("searchTerm = $0", searchTerm).first().find()
            val now = Calendar.getInstance().timeInMillis

            localSearch?.apply {
                this.timestamp = now
            } ?: run {
                copyToRealm(
                    LocalSearchItem().apply {
                        this.searchTerm = searchTerm
                        this.timestamp = now
                    }
                )

                val searches = query<LocalSearchItem>().sort("timestamp", Sort.DESCENDING).find()
                if (searches.size > 5) {
                    searches.drop(5).forEach {
                        findLatest(it)?.apply {
                            delete(this)
                        }
                    }
                }
            }
        }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        realmProvider.open().write {
            val localSearch = query<LocalSearchItem>("searchTerm = $0", searchTerm).first().find()

            localSearch?.let {
                findLatest(it)?.apply {
                    delete(this)
                }
            }
        }
    }
}