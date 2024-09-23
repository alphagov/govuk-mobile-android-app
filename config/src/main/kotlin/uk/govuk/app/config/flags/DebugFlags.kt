package uk.govuk.app.config.flags

import javax.inject.Inject

class DebugFlags @Inject constructor() {

    // Flags can be set to true, false or null (unset i.e. defer to remote flag)
    internal fun isOnboardingEnabled(): Boolean? = true
    internal fun isSearchEnabled(): Boolean? = true

}