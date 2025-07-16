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
    fun `Given the user has not previously requested permission, When is first permission request completed, then return false`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns false

        runTest {

            assertFalse(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has previously requested permission, When is first permission request completed, then return true`() {
        val repo = NotificationsRepo(notificationsDataStore)

        coEvery { notificationsDataStore.isFirstPermissionRequestCompleted() } returns true

        runTest {
            assertTrue(repo.isFirstPermissionRequestCompleted())
        }
    }

    @Test
    fun `Given the user has completed first permission request, When first permission request completed, then update data store`() {
        val repo = NotificationsRepo(notificationsDataStore)

        runTest {
            repo.firstPermissionRequestCompleted()

            coVerify { notificationsDataStore.firstPermissionRequestCompleted()}
        }
    }
}
