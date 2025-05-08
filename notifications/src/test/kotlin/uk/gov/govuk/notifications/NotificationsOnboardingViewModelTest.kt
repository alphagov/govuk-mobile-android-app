package uk.gov.govuk.notifications

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient

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
        every { notificationsClient.giveConsent() } returns Unit

        val onCompleted = slot<() -> Unit>()
        every {
            notificationsClient.requestPermission(onCompleted = capture(onCompleted))
        } answers {
            onCompleted.captured.invoke()
        }

        viewModel.onContinueClick("Title")

        runTest {
            verify(exactly = 1) {
                notificationsClient.requestPermission(onCompleted = any())

                analyticsClient.buttonClick("Title")
            }
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
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
    fun `Given Allow notifications button click, then give consent and log analytics`() {
        every { notificationsClient.giveConsent() } returns Unit

        viewModel.onGiveConsentClick("Title")

        runTest {
            verify(exactly = 1) {
                notificationsClient.giveConsent()
                analyticsClient.buttonClick("Title")
            }
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given Turn off notifications button click, then log analytics`() {
        viewModel.onTurnOffNotificationsClick("Title")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Title",
                    external = true
                )
            }
        }
    }

    @Test
    fun `Given Privacy policy link click, then log analytics`() {
        viewModel.onPrivacyPolicyClick("Text", "Url")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Text",
                    url = "Url",
                    external = true
                )
            }
        }
    }

    @Test
    fun `Given the permission status is granted, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.giveConsent() } returns Unit
        every { notificationsClient.consentGiven() } returns true

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is not granted, When init, then the ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        every { notificationsClient.requestPermission() } returns Unit

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Default)
        }
    }

    @Test
    fun `Given the permission status is granted and consent is given, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns true

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted but consent is not given, When init, then ui state should be no consent`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns false

        viewModel.updateUiState(permissionStatus)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.NoConsent)
        }
    }
}
