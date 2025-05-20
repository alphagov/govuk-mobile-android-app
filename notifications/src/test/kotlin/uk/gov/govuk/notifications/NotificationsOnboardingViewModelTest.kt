package uk.gov.govuk.notifications

import android.os.Build
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
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
class NotificationsOnboardingViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val permissionStatus = mockk<PermissionStatus>()
    private val notificationsDataStore = mockk<NotificationsDataStore>()

    private lateinit var viewModel: NotificationsOnboardingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = NotificationsOnboardingViewModel(notificationsDataStore)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given the Android version is greater than 12 and onboarding is completed, When init, then ui state should be finish`() {
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the Android version is greater than 12 and onboarding is not completed, When init, then ui state should be default`() {
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is less than 12, permission status is granted and onboarding is not completed, When init, then ui state should be default`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Default)
        }
    }

    @Test
    fun `Given the Android version is less than 12, permission status is not granted and onboarding is completed, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the Android version is less than 12, permission status is not granted and onboarding is not completed, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns false
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }

    @Test
    fun `Given the Android version is less than 12, permission status is granted and onboarding is completed, When init, then ui state should be finish`() {
        every { permissionStatus.isGranted } returns true
        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            viewModel.updateUiState(permissionStatus, Build.VERSION_CODES.S_V2)

            val result = viewModel.uiState.first()
            assertTrue(result is NotificationsUiState.Finish)
        }
    }
}
