package uk.govuk.app.config.flags.local

import javax.inject.Inject

class LocalFlagRepo @Inject constructor() {

    internal fun isSearchEnabled() = true

}