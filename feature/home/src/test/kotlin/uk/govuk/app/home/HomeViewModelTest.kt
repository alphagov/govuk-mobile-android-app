package uk.govuk.app.home

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import org.junit.Test
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.config.data.flags.FlagRepo

class HomeViewModelTest {

    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val flagRepo = mockk<FlagRepo>(relaxed = true)

    @Test
    fun `Given a page view, then log analytics`() {
        val viewModel = HomeViewModel(analyticsClient, flagRepo)

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "HomeScreen",
                screenName = "Homepage",
                title = "Homepage"
            )
        }
    }

    @Test
    fun `Given a page view, and the search flag is enabled, then return true`() {
        val viewModel = HomeViewModel(analyticsClient, flagRepo)

        every { flagRepo.isSearchEnabled() } returns true

        assert(viewModel.isSearchEnabled())
    }

    @Test
    fun `Given a page view, and the search flag is disabled, then return false`() {
        val viewModel = HomeViewModel(analyticsClient, flagRepo)

        every { flagRepo.isSearchEnabled() } returns false

        assertFalse(viewModel.isSearchEnabled())
    }
}
