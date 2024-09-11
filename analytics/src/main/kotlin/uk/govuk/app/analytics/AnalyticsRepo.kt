package uk.govuk.app.analytics

import javax.inject.Inject

class AnalyticsRepo @Inject constructor(
    private val dataStore: AnalyticsDataStore
) {
    internal suspend fun isAnalyticsEnabled() = dataStore.isAnalyticsEnabled()

    internal suspend fun analyticsEnabled() = dataStore.analyticsEnabled()

    internal suspend fun analyticsDisabled() = dataStore.analyticsDisabled()
}