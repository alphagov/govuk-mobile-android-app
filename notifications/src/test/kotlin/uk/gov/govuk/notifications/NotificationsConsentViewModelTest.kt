package uk.gov.govuk.notifications

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
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
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

@OptIn(ExperimentalPermissionsApi::class, ExperimentalCoroutinesApi::class)
class NotificationsConsentViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val permissionStatus = mockk<PermissionStatus>()
    private val notificationsClient = mockk<NotificationsClient>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsConsentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsConsentViewModel(notificationsClient, notificationsDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the permission status is not granted, When init, then remove consent called and ui state should be finish`() {
        every { permissionStatus.isGranted } returns false
        every { notificationsClient.removeConsent() } returns Unit

        runTest {
            viewModel.updateUiState(permissionStatus)

            verify(exactly = 1) {
                notificationsClient.removeConsent()
            }

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted and onboarding is not completed, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        every { notificationsClient.consentGiven() } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted, onboarding is completed and consent is given, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true
        every { notificationsClient.consentGiven() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the permission status is granted, onboarding is completed and consent is not given, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true
        every { notificationsClient.consentGiven() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given finish is called, then ui state should be finish`() {
        runTest {
            viewModel.finish()

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }
}
