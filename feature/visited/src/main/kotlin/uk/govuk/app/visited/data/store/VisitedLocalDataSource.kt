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
        // TODO: for testing only, remove this when you have a way of adding entries...
        realmProvider.open().write {
            val results = query<VisitedItem>().find()
            delete(results)
        }

        var today = LocalDate.now()

        val entries = listOf(
            VisitedItem().apply {
                title = "GOV.UK"
                url = "https://www.gov.uk"
                lastVisited = today.toEpochDay()
            },
            VisitedItem().apply {
                title = "Amazon"
                url = "https://www.amazon.co.uk"
                lastVisited = today.minusDays(4).toEpochDay()
            },
            VisitedItem().apply {
                title = "Google"
                url = "https://www.google.com"
                lastVisited = today.minusMonths(2).toEpochDay()
            },
            VisitedItem().apply {
                title = "Trello"
                url = "https://www.trello.com"
                lastVisited = today.minusYears(1).toEpochDay()
            }
        )

        for (entry in entries) {
            createVisitedItem(entry)
        }

        emitAll(
            realmProvider.open().query<VisitedItem>().asFlow().map {
                it.list.sortedByDescending { it.lastVisited }
            }
        )
    }

    private suspend fun createVisitedItem(visitedItem: VisitedItem) {
        realmProvider.open().write {
//            TODO: when the above fake entries are removed, uncomment this
//            visitedItem.lastVisited = LocalDate.now().toEpochDay()
            copyToRealm(visitedItem)
        }
    }
}
