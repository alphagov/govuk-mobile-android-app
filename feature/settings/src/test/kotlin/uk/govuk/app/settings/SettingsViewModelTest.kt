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
import uk.govuk.app.settings.BuildConfig.ACCESSIBILITY_STATEMENT_EVENT
import uk.govuk.app.settings.BuildConfig.ACCESSIBILITY_STATEMENT_URL
import uk.govuk.app.settings.BuildConfig.HELP_AND_FEEDBACK_EVENT
import uk.govuk.app.settings.BuildConfig.HELP_AND_FEEDBACK_URL
import uk.govuk.app.settings.BuildConfig.OPEN_SOURCE_LICENCE_EVENT
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_EVENT
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.govuk.app.settings.BuildConfig.TERMS_AND_CONDITIONS_EVENT
import uk.govuk.app.settings.BuildConfig.TERMS_AND_CONDITIONS_URL

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
        coEvery { analytics.isAnalyticsEnabled() } returns true

        val viewModel = SettingsViewModel(analytics)

        runTest {
            val result = viewModel.uiState.first()
            assertTrue(result!!.isAnalyticsEnabled)
        }
    }

    @Test
    fun `Given analytics are disabled, When init, then return analytics disabled`() {
        coEvery { analytics.isAnalyticsEnabled() } returns false

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

    @Test
    fun `Given a license page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onLicenseView()

        verify {
            analytics.screenView(
                screenClass = OPEN_SOURCE_LICENCE_EVENT,
                screenName = OPEN_SOURCE_LICENCE_EVENT,
                title = OPEN_SOURCE_LICENCE_EVENT
            )
        }
    }

    @Test
    fun `Given a help and feedback page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onHelpAndFeedbackView()

        verify {
            analytics.settingsItemClick(
                text = HELP_AND_FEEDBACK_EVENT,
                url = HELP_AND_FEEDBACK_URL
            )
        }
    }

    @Test
    fun `Given a privacy policy page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onPrivacyPolicyView()

        verify {
            analytics.settingsItemClick(
                text = PRIVACY_POLICY_EVENT,
                url = PRIVACY_POLICY_URL
            )
        }
    }

    @Test
    fun `Given a accessibility statement page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onAccessibilityStatementView()

        analytics.settingsItemClick(
            text = ACCESSIBILITY_STATEMENT_EVENT,
            url = ACCESSIBILITY_STATEMENT_URL
        )
    }

    @Test
    fun `Given a terms and conditions page view, then log analytics`() {
        val viewModel = SettingsViewModel(analytics)

        viewModel.onTermsAndConditionsView()

        analytics.settingsItemClick(
            text = TERMS_AND_CONDITIONS_EVENT,
            url = TERMS_AND_CONDITIONS_URL
        )
    }
}
