package uk.govuk.app.settings

import io.mockk.coEvery
import io.mockk.coVerify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.analytics.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.AnalyticsEnabledState.NOT_SET

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val analytics = mockk<Analytics>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given analytics are enabled, When init, then return analytics enabled`() {
        coEvery { analytics.getAnalyticsEnabledState() } returns ENABLED

        val viewModel = SettingsViewModel(analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given analytics are disabled, When init, then return analytics disabled`() {
        coEvery { analytics.getAnalyticsEnabledState() } returns DISABLED

        val viewModel = SettingsViewModel(analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given analytics are not set, When init, then return analytics disabled`() {
        coEvery { analytics.getAnalyticsEnabledState() } returns NOT_SET

        val viewModel = SettingsViewModel(analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertFalse(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onPageView()

        verify {
            analytics.screenView(
                screenClass = "SettingsScreen",
                screenName = "Settings",
                title = "Settings"
            )
        }
    }

    @Test
    fun `Given analytics have been enabled, then update and emit ui state`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onAnalyticsConsentChanged(true)

        runTest {
            val result = viewModel.uiState.value
            assertTrue(result!!.isAnalyticsEnabled)

            coVerify {
                analytics.enable()
            }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update and emit ui state`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onAnalyticsConsentChanged(false)

        runTest {
            val result = viewModel.uiState.value
            assertFalse(result!!.isAnalyticsEnabled)

            coVerify {
                analytics.disable()
            }
        }
    }
}