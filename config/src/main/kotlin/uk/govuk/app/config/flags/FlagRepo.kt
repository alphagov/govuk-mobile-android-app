package uk.govuk.app.config.flags

import uk.govuk.app.config.ConfigRepo
import uk.govuk.config.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlagRepo @Inject constructor(
    private val debugFlags: DebugFlags,
    private val configRepo: ConfigRepo
) {

    fun isOnboardingEnabled(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isOnboardingEnabled,
            remoteFlag = configRepo.config.releaseFlags.onboarding
        )
    }

    fun isSearchEnabled(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isSearchEnabled,
            remoteFlag = configRepo.config.releaseFlags.search
        )
    }
}

internal fun isEnabled(
    debugEnabled:Boolean = BuildConfig.DEBUG,
    debugFlag: Boolean?,
    remoteFlag: Boolean
): Boolean {
    return if (debugEnabled) debugFlag ?: remoteFlag else remoteFlag
}
