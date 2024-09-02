package uk.govuk.app

import javax.inject.Inject

internal class AppRepo @Inject constructor(
    private val appDataStore: AppDataStore
) {
    internal suspend fun isOnboardingCompleted() = appDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = appDataStore.onboardingCompleted()
}