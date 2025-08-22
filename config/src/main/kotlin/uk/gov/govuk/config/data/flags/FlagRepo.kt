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
            remoteFlag = configRepo.config.releaseFlags.notifications
        )
    }

    fun isLocalServicesEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isLocalServicesEnabled,
            remoteFlag = configRepo.config.releaseFlags.localServices
        )
    }

    fun isExternalBrowserEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = debugFlags.isExternalBrowserEnabled,
            remoteFlag = configRepo.config.releaseFlags.externalBrowser
        )
    }

    fun isChatEnabled(): Boolean {
        return isEnabled(
            debugEnabled = debugEnabled,
            debugFlag = true, // Dev only flag, only set to true when actively working on chat
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
