package uk.govuk.app.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepo @Inject constructor(
    private val dataStore: AnalyticsDataStore
) {
    internal suspend fun getAnalyticsEnabledState() = dataStore.getAnalyticsEnabledState()

    internal suspend fun analyticsEnabled() = dataStore.analyticsEnabled()

    internal suspend fun analyticsDisabled() = dataStore.analyticsDisabled()
}