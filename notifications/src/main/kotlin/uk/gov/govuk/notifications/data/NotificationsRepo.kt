package uk.gov.govuk.notifications.data

import uk.gov.govuk.notifications.data.local.NotificationsDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class NotificationsRepo @Inject constructor(
    private val notificationsDataStore: NotificationsDataStore
) {
    internal suspend fun isOnboardingSeen() = notificationsDataStore.isOnboardingSeen()

    internal suspend fun onboardingSeen() = notificationsDataStore.onboardingSeen()
}
