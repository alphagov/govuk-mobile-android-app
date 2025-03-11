package uk.gov.govuk.data

import uk.gov.govuk.data.local.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppRepo @Inject constructor(
    private val appDataStore: AppDataStore
) {
    internal suspend fun isOnboardingCompleted() = appDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = appDataStore.onboardingCompleted()

    internal suspend fun isTopicSelectionCompleted() = appDataStore.isTopicSelectionCompleted()

    internal suspend fun topicSelectionCompleted() = appDataStore.topicSelectionCompleted()
}