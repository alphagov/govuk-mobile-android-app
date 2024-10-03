package uk.govuk.app.config.flags

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebugFlags @Inject constructor() {

    // Flags can be set to true, false or null (unset i.e. defer to remote flag)
    internal val isOnboardingEnabled: Boolean? = true
    internal val isSearchEnabled: Boolean? = true
    internal val isTopicsEnabled: Boolean? = true

}