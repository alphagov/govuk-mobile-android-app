package uk.govuk.app.config.data.flags

import uk.govuk.app.config.data.ConfigRepo
import uk.govuk.app.config.extensions.isVersionLessThan
import uk.govuk.config.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlagRepo @Inject constructor(
    private val debugFlags: DebugFlags,
    private val configRepo: ConfigRepo
) {
    fun isAppAvailable(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isAppAvailable,
            remoteFlag = configRepo.config.available
        )
    }

    fun isRecommendUpdate(appVersion: String): Boolean {
        val debugRecommendedVersion = if (debugFlags.recommendedVersion != null) {
            appVersion.isVersionLessThan(debugFlags.recommendedVersion)
        } else {
            null
        }
        return isEnabled(
            debugFlag = debugRecommendedVersion,
            remoteFlag = appVersion.isVersionLessThan(configRepo.config.recommendedVersion)
        )
    }

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

    fun isRecentActivityEnabled(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isRecentActivityEnabled,
            remoteFlag = configRepo.config.releaseFlags.recentActivity
        )
    }

    fun isTopicsEnabled(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isTopicsEnabled,
            remoteFlag = configRepo.config.releaseFlags.topics
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
