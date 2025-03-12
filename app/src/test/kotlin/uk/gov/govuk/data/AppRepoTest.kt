package uk.gov.govuk.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.data.local.AppDataStore

class AppRepoTest {

    private val appDataStore = mockk<AppDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed onboarding, When is onboarding completed, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isOnboardingCompleted() } returns false

        runTest {

            assertFalse(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed onboarding, When is onboarding completed, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isOnboardingCompleted() } returns true

        runTest {
            assertTrue(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed onboarding, When onboarding completed, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.onboardingCompleted()

            coVerify { appDataStore.onboardingCompleted() }
        }
    }

    @Test
    fun `Given the user has not previously completed topic selection, When is topic selection completed, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns false

        runTest {

            assertFalse(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed topic selection, When is topic selection completed, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns true

        runTest {
            assertTrue(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has completed topic selection, When topic selection completed, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.topicSelectionCompleted()

            coVerify { appDataStore.topicSelectionCompleted() }
        }
    }
}