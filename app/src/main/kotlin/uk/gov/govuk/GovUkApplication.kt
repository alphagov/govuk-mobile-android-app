package uk.gov.govuk

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import uk.gov.govuk.notifications.NotificationsClient
import javax.inject.Inject

@HiltAndroidApp
class GovUkApplication: Application() {

    @Inject lateinit var notificationsClient: NotificationsClient

    override fun onCreate() {
        super.onCreate()
        notificationsClient.initialise(this, BuildConfig.ONE_SIGNAL_APP_ID)
    }
}
