package uk.govuk.app.analytics.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.DISABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.ENABLED
import uk.govuk.app.analytics.data.local.AnalyticsEnabledState.NOT_SET
import uk.govuk.app.analytics.data.local.AnalyticsDataStore

class AnalyticsRepoTest {

    private val dataStore = mockk<AnalyticsDataStore>(relaxed = true)

    @Test
    fun `Given analytics are not set, then return not set`() {
        val repo = AnalyticsRepo(dataStore)

        coEvery { dataStore.getAnalyticsEnabledState() } returns NOT_SET

        runTest {
            assertEquals(NOT_SET, repo.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics are enabled, then return enabled`() {
        val repo = AnalyticsRepo(dataStore)

        coEvery { dataStore.getAnalyticsEnabledState() } returns ENABLED

        runTest {
            assertEquals(ENABLED, repo.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics are disabled, then return disabled`() {
        val repo = AnalyticsRepo(dataStore)

        coEvery { dataStore.getAnalyticsEnabledState() } returns DISABLED

        runTest {
            assertEquals(DISABLED, repo.getAnalyticsEnabledState())
        }
    }

    @Test
    fun `Given analytics have been enabled, then update data store`() {
        val repo = AnalyticsRepo(dataStore)

        runTest {
            repo.analyticsEnabled()

            coVerify { dataStore.analyticsEnabled() }
        }
    }

    @Test
    fun `Given analytics have been disabled, then update data store`() {
        val repo = AnalyticsRepo(dataStore)

        runTest {
            repo.analyticsDisabled()

            coVerify { dataStore.analyticsDisabled() }
        }
    }
}