package uk.govuk.app.notifications

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient

class NotificationsPermissionViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val notificationsClient = mockk<NotificationsClient>()

    private lateinit var viewModel: NotificationsPermissionViewModel

    @Before
    fun setup() {
        viewModel = NotificationsPermissionViewModel(analyticsClient, notificationsClient)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        verify(exactly = 1) {
            analyticsClient.screenView(
                screenClass = "NotificationsPermissionScreen",
                screenName = "NotificationsPermissionScreen",
                title = "NotificationsPermissionScreen"
            )
        }
    }

    @Test
    fun `Given Im In button click, then request permission and log analytics`() {
        every { notificationsClient.requestPermission() } returns Unit

        viewModel.onContinueClick("Title")

        verify(exactly = 1) {
            notificationsClient.requestPermission()

            analyticsClient.buttonClick("Title")
        }
    }

    @Test
    fun `Given Skip button click, then log analytics`() {
        viewModel.onSkipClick("Title")

        verify(exactly = 1) {
            analyticsClient.buttonClick("Title")
        }
    }
}
