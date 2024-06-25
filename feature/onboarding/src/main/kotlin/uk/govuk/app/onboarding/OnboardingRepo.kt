package uk.govuk.app.onboarding

import javax.inject.Inject

internal class OnboardingRepo @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore
) {
    internal suspend fun isOnboardingCompleted() = onboardingDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = onboardingDataStore.onboardingCompleted()
}