package uk.govuk.app.visited

import uk.govuk.app.visited.data.store.VisitedLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedClient @Inject constructor(
    private val visitedLocalDataSource: VisitedLocalDataSource
): Visited {
    override suspend fun visitableItemClick(title: String, url: String) {
        upsertVisitedItem(title, url)
    }

    private suspend fun upsertVisitedItem(title: String, url: String) {
        visitedLocalDataSource.insertOrUpdate(title, url)
    }
}
