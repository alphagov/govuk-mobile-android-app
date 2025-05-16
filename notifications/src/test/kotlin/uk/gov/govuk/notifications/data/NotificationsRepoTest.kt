package uk.gov.govuk.notifications.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.notifications.data.local.NotificationsDataStore

class NotificationsRepoTest {

    private val notificationsDataStore = mockk<NotificationsDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed onboarding, When is onboarding completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isOnboardingCompleted() } returns false

        runTest {

            assertFalse(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed onboarding, When is onboarding completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isOnboardingCompleted() } returns true

        runTest {
            assertTrue(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed onboarding, When onboarding completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore)

        runTest {
            repo.onboardingCompleted()

            coVerify { notificationsDataStore.onboardingCompleted() }
        }
    }
}
