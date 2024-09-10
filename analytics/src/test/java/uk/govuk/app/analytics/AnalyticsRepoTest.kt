package uk.govuk.app.analytics

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalyticsRepoTest {

    private val dataStore = mockk<AnalyticsDataStore>(relaxed = true)

    @Test
    fun `Given analytics are enabled, then return true`() {
        val repo = AnalyticsRepo(dataStore)

        coEvery { dataStore.isAnalyticsEnabled() } returns true

        runTest {
            assertTrue(repo.isAnalyticsEnabled())
        }
    }

    @Test
    fun `Given analytics are disabled, then return false`() {
        val repo = AnalyticsRepo(dataStore)

        coEvery { dataStore.isAnalyticsEnabled() } returns false

        runTest {
            assertFalse(repo.isAnalyticsEnabled())
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