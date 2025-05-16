package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
class NotificationsOnboardingViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val permissionStatus = mockk<PermissionStatus>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val notificationsClient = mockk<NotificationsClient>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsOnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsOnboardingViewModel(analyticsClient, notificationsClient, notificationsDataStore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
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
    fun `Given continue button click, then onboarding completed, request permission and log analytics`() {
        coEvery { notificationsDataStore.onboardingCompleted() } returns Unit
        every { notificationsClient.giveConsent() } returns Unit

        val onCompleted = slot<() -> Unit>()
        every {
            notificationsClient.requestPermission(onCompleted = capture(onCompleted))
        } answers {
            onCompleted.captured.invoke()
        }

        viewModel.onContinueClick("Title")

        runTest {
            coVerify(exactly = 1) {
                notificationsDataStore.onboardingCompleted()
            }
            verify(exactly = 1) {
                notificationsClient.requestPermission(onCompleted = any())

                analyticsClient.buttonClick("Title")
            }
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given Skip button click, then onboarding completed and log analytics`() {
        coEvery { notificationsDataStore.onboardingCompleted() } returns Unit

        viewModel.onSkipClick("Title")

        runTest {
            coVerify(exactly = 1) {
                notificationsDataStore.onboardingCompleted()
            }
            verify(exactly = 1) {
                analyticsClient.buttonClick("Title")
            }
        }
    }

    @Test
    fun `Given Allow notifications button click, then onboarding completed, give consent and log analytics`() {
        coEvery { notificationsDataStore.onboardingCompleted() } returns Unit
        every { notificationsClient.giveConsent() } returns Unit

        viewModel.onGiveConsentClick("Title")

        runTest {
            coVerify(exactly = 1) {
                notificationsDataStore.onboardingCompleted()
            }
            verify(exactly = 1) {
                notificationsClient.giveConsent()
                analyticsClient.buttonClick("Title")
            }
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given Turn off notifications button click, then onboarding completed and log analytics`() {
        coEvery { notificationsDataStore.onboardingCompleted() } returns Unit
        viewModel.onTurnOffNotificationsClick("Title")

        runTest {
            coVerify(exactly = 1) {
                notificationsDataStore.onboardingCompleted()
            }
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
    fun `Given the permission status is granted and onboarding completed, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.giveConsent() } returns Unit
        every { notificationsClient.consentGiven() } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is not granted and onboarding not seen, When init, then the ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        every { notificationsClient.requestPermission() } returns Unit
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Default)
        }
    }

    @Test
    fun `Given the permission status is granted, consent is given and onboarding is seen, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted, onboarding is seen but consent is not given, When init, then ui state should be no consent`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns false
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.NoConsent)
        }
    }

    @Test
    fun `Given the permission status is granted, onboarding is not seen and Android version is less than Tiramisu, When init, then ui state should be no consent`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns false
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.NoConsent)
        }
    }

    @Test
    fun `Given the permission status is not granted, onboarding is seen, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted, Android version is Tiramisu, consent is given and onboarding is not seen, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Default)
        }
    }

    @Test
    fun `Given the onboarding screen is launched from settings, When init, then ui state should be default`() {
        runTest {
            viewModel.updateUiState(permissionStatus, fromSettings = true)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsOnboardingUiState.Default)
        }
    }
}
