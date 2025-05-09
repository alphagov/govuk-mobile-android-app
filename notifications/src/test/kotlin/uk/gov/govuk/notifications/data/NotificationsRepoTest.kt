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
    fun `Given the user has not previously seen onboarding, When is onboarding seen, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isOnboardingSeen() } returns false

        runTest {

            assertFalse(repo.isOnboardingSeen())
        }
    }

    @Test
    fun `Given the user has previously seen onboarding, When is onboarding seen, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isOnboardingSeen() } returns true

        runTest {
            assertTrue(repo.isOnboardingSeen())
        }
    }

    @Test
    fun `Given the user has seen onboarding, When onboarding seen, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore)

        runTest {
            repo.onboardingSeen()

            coVerify { notificationsDataStore.onboardingSeen() }
        }
    }
}
