package uk.govuk.app.data

import uk.govuk.app.data.local.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AppRepo @Inject constructor(
    private val appDataStore: AppDataStore
) {
    internal suspend fun isOnboardingCompleted() = appDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = appDataStore.onboardingCompleted()
}