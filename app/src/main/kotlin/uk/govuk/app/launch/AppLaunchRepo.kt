package uk.govuk.app.launch

import javax.inject.Inject

internal class AppLaunchRepo @Inject constructor(
    private val appLaunchDataStore: AppLaunchDataStore
) {
    internal suspend fun isOnboardingCompleted() = appLaunchDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = appLaunchDataStore.onboardingCompleted()
}