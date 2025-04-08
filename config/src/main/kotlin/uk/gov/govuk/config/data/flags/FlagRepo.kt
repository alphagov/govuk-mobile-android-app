package uk.gov.govuk.config.data.flags

import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.extensions.isVersionLessThan
import uk.gov.govuk.config.BuildConfig
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

    fun isForcedUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugFlag = debugFlags.minimumVersion?.let {
                appVersion.isVersionLessThan(it)
            },
            remoteFlag = appVersion.isVersionLessThan(configRepo.config.minimumVersion)
        )
    }

    fun isRecommendUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugFlag = debugFlags.recommendedVersion?.let {
                appVersion.isVersionLessThan(it)
            },
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

    fun isNotificationsEnabled(): Boolean {
        return isEnabled(
            debugFlag = debugFlags.isNotificationsEnabled,
            remoteFlag = false // TODO - GOVUKAPP-1095 requires this is hardcoded 'off' for now
        )
    }

    fun isLocalEnabled(): Boolean {
        return true
//        TODO: add feature flag for local
//        return isEnabled(
//            debugFlag = debugFlags.isLocalEnabled,
//            remoteFlag = configRepo.config.releaseFlags.local
//        )
    }
}

internal fun isEnabled(
    debugEnabled:Boolean = BuildConfig.DEBUG,
    debugFlag: Boolean?,
    remoteFlag: Boolean
): Boolean {
    return if (debugEnabled) debugFlag ?: remoteFlag else remoteFlag
}
