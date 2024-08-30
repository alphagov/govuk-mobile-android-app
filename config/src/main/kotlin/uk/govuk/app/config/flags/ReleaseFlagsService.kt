package uk.govuk.app.config.flags

import uk.govuk.app.config.flags.local.LocalFlagRepo
import uk.govuk.app.config.flags.remote.RemoteFlagRepo
import javax.inject.Inject

class ReleaseFlagsService @Inject constructor(
    private val localFlagRepo: LocalFlagRepo,
    private val remoteFlagRepo: RemoteFlagRepo
) {

    fun isSearchEnabled(): Boolean {
        return isEnabled(
            remoteFlag = remoteFlagRepo.isSearchEnabled(),
            localFlag = localFlagRepo.isSearchEnabled()
        )
    }

    private fun isEnabled(
        remoteFlag: Boolean?,
        localFlag: Boolean
    ): Boolean {
        return remoteFlag ?: localFlag
    }
}
