package uk.gov.govuk.login.data

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.login.data.remote.LoginApi

class LoginRepoTest {
    private val loginApi = mockk<LoginApi>(relaxed = true)
    private lateinit var loginRepo: LoginRepo

    @Before
    fun setup() {
        loginRepo = LoginRepo(loginApi)
    }

    @Test
    fun `loginRepo returns the correct token`() {
        assertEquals("some-random-token", loginRepo.accessToken)
    }

    @Test
    fun `loginRepo does not return a null token`() {
        assertNotNull(loginRepo.accessToken)
    }

    @Test
    fun `loginRepo does not return an empty token`() {
        assertNotEquals("", loginRepo.accessToken)
    }
}
