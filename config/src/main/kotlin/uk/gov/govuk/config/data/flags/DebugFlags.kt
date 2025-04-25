package uk.gov.govuk.config.data.flags

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugFlags @Inject constructor() {

    // Flags can be set to true, false or null (unset i.e. defer to remote flag)
    internal val isAppAvailable: Boolean? = true
    internal val minimumVersion: String? = "0.0.1"
    internal val recommendedVersion: String? = "0.0.1"
    internal val isOnboardingEnabled: Boolean? = true
    internal val isSearchEnabled: Boolean? = false
    internal val isRecentActivityEnabled: Boolean? = true
    internal val isTopicsEnabled: Boolean? = true
    internal val isNotificationsEnabled: Boolean? = true
    internal val isLocalServicesEnabled: Boolean? = true

}