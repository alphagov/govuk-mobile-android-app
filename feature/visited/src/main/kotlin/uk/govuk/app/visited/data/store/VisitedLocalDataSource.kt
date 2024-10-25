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

    suspend fun insertOrUpdate(title: String, url: String, lastVisited: LocalDate = LocalDate.now()) {
        println("Date: $lastVisited")

        realmProvider.open().write {
            val visitedItem = query<VisitedItem>("title = $0 AND url = $1", title, url).first().find()

            if (visitedItem != null) {
                println("VisitedItem: ${visitedItem.lastVisited}")
            }

            visitedItem?.apply {
                this.lastVisited = lastVisited.toEpochDay()
            } ?: copyToRealm(
                VisitedItem().apply {
                    this.title = title
                    this.url = url
                    this.lastVisited = lastVisited.toEpochDay()
                }
            )
        }
    }
}
