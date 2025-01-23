package uk.govuk.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uk.govuk.app.notifications.NotificationsClient
import javax.inject.Inject

@HiltAndroidApp
class GovUkApplication: Application() {

    @Inject
    val notificationsClient = NotificationsClient()

    override fun onCreate() {
        super.onCreate()

        notificationsClient.initialise(this)
    }
}
