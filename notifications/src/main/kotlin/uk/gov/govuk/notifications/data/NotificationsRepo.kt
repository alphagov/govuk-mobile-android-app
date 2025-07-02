package uk.gov.govuk.notifications.data

import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore
) {
    internal suspend fun isOnboardingCompleted() = notificationsDataStore.isOnboardingCompleted()

    internal suspend fun onboardingCompleted() = notificationsDataStore.onboardingCompleted()

    internal suspend fun isFirstPermissionRequestCompleted() =
        notificationsDataStore.isFirstPermissionRequestCompleted()

    internal suspend fun firstPermissionRequestCompleted() =
        notificationsDataStore.firstPermissionRequestCompleted()
}
