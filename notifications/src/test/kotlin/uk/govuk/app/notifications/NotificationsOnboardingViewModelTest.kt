package uk.govuk.app.notifications

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient

@OptIn(ExperimentalPermissionsApi::class)
class NotificationsOnboardingViewModelTest {
    private val permissionStatus = mockk<PermissionStatus>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val notificationsClient = mockk<NotificationsClient>()

    private lateinit var viewModel: NotificationsOnboardingViewModel

    @Before
    fun setup() {
        viewModel = NotificationsOnboardingViewModel(analyticsClient, notificationsClient)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        viewModel.onPageView()

        runTest {
            verify(exactly = 1) {
                analyticsClient.screenView(
                    screenClass = "NotificationsOnboardingScreen",
                    screenName = "NotificationsOnboardingScreen",
                    title = "NotificationsOnboardingScreen"
                )
            }
        }
    }

    @Test
    fun `Given continue button click, then request permission and log analytics`() {
        every { notificationsClient.requestPermission() } returns Unit

        viewModel.onContinueClick("Title")

        runTest {
            verify(exactly = 1) {
                notificationsClient.requestPermission()

                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given Skip button click, then log analytics`() {
        viewModel.onSkipClick("Title")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given the permission status is granted, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.giveConsent() } returns Unit

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)

            verify(exactly = 1) {
                notificationsClient.giveConsent()
            }
        }
    }

    @Test
    fun `Given the permission status is not granted, When init, then permission is requested and ui state should be finish`() {
        every { permissionStatus.isGranted } returns false
        every { permissionStatus.shouldShowRationale } returns false
        every { notificationsClient.requestPermission() } returns Unit

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)

            verify(exactly = 1) {
                notificationsClient.requestPermission()
            }
        }
    }

    @Test
    fun `Given the permission status is should show rationale, When init, then ui state should be opt in`() {
        every { permissionStatus.shouldShowRationale } returns true

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Default)
        }
    }
}
