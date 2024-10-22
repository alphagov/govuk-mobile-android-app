package uk.govuk.app.visited.data

import uk.govuk.app.visited.data.store.VisitedLocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedRepo @Inject constructor(
    localDataSource: VisitedLocalDataSource,
) {
    val visitedItems = localDataSource.visitedItems
}
