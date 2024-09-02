package uk.govuk.app

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
}