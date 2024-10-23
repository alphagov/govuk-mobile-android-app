package uk.govuk.app.visited.data.store

import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.visited.data.model.VisitedItem
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedLocalDataSource @Inject constructor(
    private val realmProvider: VisitedRealmProvider
) {

    val visitedItems: Flow<List<VisitedItem>> = flow {
        emitAll(
            realmProvider.open().query<VisitedItem>().asFlow().map {
                it.list.sortedByDescending { it.lastVisited }
            }
        )
    }

    private suspend fun createVisitedItem(visitedItem: VisitedItem) {
        realmProvider.open().write {
            visitedItem.lastVisited = LocalDate.now().toEpochDay()
            copyToRealm(visitedItem)
        }
    }
}
