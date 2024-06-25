package uk.govuk.app.onboarding

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingRepoTest {

    private val onboardingDataStore = mockk<OnboardingDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed onboarding and the data store is empty, When is onboarding completed, then return false`() {
        val repo = OnboardingRepo(onboardingDataStore)

        coEvery { onboardingDataStore.isOnboardingCompleted() } returns false

        runTest {

            assertFalse(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed onboarding, When is onboarding completed, then return true`() {
        val repo = OnboardingRepo(onboardingDataStore)

        coEvery { onboardingDataStore.isOnboardingCompleted() } returns true

        runTest {
            assertTrue(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed onboarding, When onboarding completed, then update data store`() {
        val repo = OnboardingRepo(onboardingDataStore)

        runTest {
            repo.onboardingCompleted()

            coVerify { onboardingDataStore.onboardingCompleted() }
        }
    }
}