package uk.gov.govuk.login.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.login.data.local.LoginDataStore

class LoginRepoTest {
    private val loginDataStore = mockk<LoginDataStore>(relaxed = true)

    @Test
    fun `Given the there is no refresh token expiry date stored, When get refresh token expiry date, then return null`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenExpiryDate() } returns null

        runTest {

            assertNull(repo.getIdTokenIssueDate())
        }
    }

    @Test
    fun `Given the there is a refresh token expiry date stored, When get refresh token expiry date, then return expiry date`() {
        val repo = LoginRepo(loginDataStore)

        coEvery { loginDataStore.getRefreshTokenExpiryDate() } returns 12345L

        runTest {

            assertEquals(12345L, repo.getIdTokenIssueDate())
        }
    }

    @Test
    fun `Given a refresh token expiry date is 12345, When set refresh token expiry date, then update data store`() {
        val repo = LoginRepo(loginDataStore)

        runTest {
            repo.setRefreshTokenExpiryDate(12345L)

            coVerify { loginDataStore.setRefreshTokenExpiryDate(12345L) }
        }
    }

    @Test
    fun `Given a different user has logged in, When clear, then clear data store`() {
        val repo = LoginRepo(loginDataStore)

        runTest {
            repo.clear()

            coVerify { loginDataStore.clear() }
        }
    }
}
