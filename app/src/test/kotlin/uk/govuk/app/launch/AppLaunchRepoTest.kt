package uk.govuk.app.launch

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLaunchRepoTest {

    private val appLaunchDataStore = mockk<AppLaunchDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed onboarding, When is onboarding completed, then return false`() {
        val repo = AppLaunchRepo(appLaunchDataStore)

        coEvery { appLaunchDataStore.isOnboardingCompleted() } returns false

        runTest {

            assertFalse(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed onboarding, When is onboarding completed, then return true`() {
        val repo = AppLaunchRepo(appLaunchDataStore)

        coEvery { appLaunchDataStore.isOnboardingCompleted() } returns true

        runTest {
            assertTrue(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed onboarding, When onboarding completed, then update data store`() {
        val repo = AppLaunchRepo(appLaunchDataStore)

        runTest {
            repo.onboardingCompleted()

            coVerify { appLaunchDataStore.onboardingCompleted() }
        }
    }
}