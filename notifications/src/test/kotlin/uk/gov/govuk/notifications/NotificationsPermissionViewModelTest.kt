package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
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
class NotificationsPermissionViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val permissionStatus = mockk<PermissionStatus>()
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val notificationsClient = mockk<NotificationsClient>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsPermissionViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsPermissionViewModel(analyticsClient, notificationsClient, notificationsDataStore)
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
    fun `Given continue button click, then onboarding completed, first permission completed, request permission and log analytics`() {
        coEvery { notificationsDataStore.onboardingCompleted() } returns Unit
        coEvery { notificationsDataStore.firstPermissionRequestCompleted() } returns Unit
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
                notificationsDataStore.firstPermissionRequestCompleted()
            }
            verify(exactly = 1) {
                notificationsClient.requestPermission(onCompleted = any())

                analyticsClient.buttonClick("Title")
            }
            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
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
    fun `Given Alert button click, then log analytics`() {
        viewModel.onAlertButtonClick("Text")

        runTest {
            verify(exactly = 1) {
                analyticsClient.buttonClick(
                    text = "Text"
                )
            }
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is not completed and should show rationale is false, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted()} returns false
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is completed and should show rationale is true, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted()} returns true
        every { permissionStatus.shouldShowRationale } returns true

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is not granted, first permission request is completed and should show rationale is false, When init, then ui state should be alert`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted()} returns true
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }

    @Test
    fun `Given the Android version is greater than 12, permission status is granted, first permission request is not completed and should show rationale is false, When init, then ui state should be alert`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted()} returns false
        every { permissionStatus.shouldShowRationale } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.TIRAMISU)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }

    @Test
    fun `Given the Android version is less than 12, When init, then ui state should be alert`() {
        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Alert)
        }
    }
}
