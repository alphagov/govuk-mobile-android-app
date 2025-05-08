package uk.gov.govuk.config.data.flags

import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.extensions.isVersionLessThan
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlagRepo @Inject constructor(
    private val debugEnabled: Boolean,
    private val debugFlags: DebugFlags,
    private val configRepo: ConfigRepo
) {
    fun isAppAvailable(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isAppAvailable,
            remoteFlag = configRepo.config.available
        )
    }

    fun isForcedUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.minimumVersion?.let {
                appVersion.isVersionLessThan(it)
            },
            remoteFlag = appVersion.isVersionLessThan(configRepo.config.minimumVersion)
        )
    }

    fun isRecommendUpdate(appVersion: String): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.recommendedVersion?.let {
                appVersion.isVersionLessThan(it)
            },
            remoteFlag = appVersion.isVersionLessThan(configRepo.config.recommendedVersion)
        )
    }

    fun isOnboardingEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isOnboardingEnabled,
            remoteFlag = configRepo.config.releaseFlags.onboarding
        )
    }

    fun isSearchEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isSearchEnabled,
            remoteFlag = configRepo.config.releaseFlags.search
        )
    }

    fun isRecentActivityEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isRecentActivityEnabled,
            remoteFlag = configRepo.config.releaseFlags.recentActivity
        )
    }

    fun isTopicsEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isTopicsEnabled,
            remoteFlag = configRepo.config.releaseFlags.topics
        )
    }

    fun isNotificationsEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isNotificationsEnabled,
            remoteFlag = false // TODO - GOVUKAPP-1095 requires this is hardcoded 'off' for now
        )
    }

    fun isLocalServicesEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isLocalServicesEnabled,
            remoteFlag = false // TODO - Hardcoded off
        )
    }

    fun isLoginEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = false, // Dev only flag, only set to true when actively working on login
            remoteFlag = false // Dev only flag, always off for production builds!!!
        )
    }
}

internal fun isEnabled(
    debugEnabled: Boolean,
    debugFlag: Boolean?,
    remoteFlag: Boolean
): Boolean {
    return if (debugEnabled) debugFlag ?: remoteFlag else remoteFlag
}
