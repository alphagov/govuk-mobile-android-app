package uk.govuk.app.settings

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import uk.govuk.app.analytics.Analytics

class SettingsViewModelTest {

    private val analytics = mockk<Analytics>(relaxed = true)

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
}