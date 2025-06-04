package uk.gov.govuk.data

import uk.gov.govuk.data.local.AppDataStore
import uk.gov.govuk.ui.model.HomeWidget
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppRepo @Inject constructor(
    private val appDataStore: AppDataStore
) {
    internal suspend fun isTopicSelectionCompleted() = appDataStore.isTopicSelectionCompleted()

    internal suspend fun topicSelectionCompleted() = appDataStore.topicSelectionCompleted()

    internal val suppressedHomeWidgets = appDataStore.suppressedHomeWidgets

    internal suspend fun suppressHomeWidget(widget: HomeWidget) = appDataStore.suppressHomeWidget(widget)

    internal suspend fun clear() = appDataStore.clear()

}
