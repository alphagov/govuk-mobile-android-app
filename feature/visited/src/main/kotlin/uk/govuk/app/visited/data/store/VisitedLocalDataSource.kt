package uk.govuk.app.visited.data.store

import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.visited.data.model.VisitedItem
import java.util.Date
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

        val entries = mapOf(
            "GOV.UK" to "https://www.gov.uk",
            "Amazon" to "https://www.amazon.co.uk",
            "Google" to "https://www.google.com"
        )

        for (entry in entries) {
            println("${entry.key} : ${entry.value}")

            val item = VisitedItem().apply {
                title = entry.key
                url = entry.value
            }
            createVisitedItem(item)
        }

        emitAll(realmProvider.open().query<VisitedItem>().asFlow().map { it.list })
    }

    private suspend fun createVisitedItem(visitedItem: VisitedItem) {
        realmProvider.open().write {
            visitedItem.lastVisited = Date().toString()
            copyToRealm(visitedItem)
        }
    }
}
