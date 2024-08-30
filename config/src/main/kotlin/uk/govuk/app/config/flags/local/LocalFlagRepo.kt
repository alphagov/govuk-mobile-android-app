package uk.govuk.app.config.flags.local

import javax.inject.Inject

class LocalFlagRepo @Inject constructor() {

    fun isSearchEnabled() = true

}