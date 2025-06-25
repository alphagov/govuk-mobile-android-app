package uk.gov.govuk.auth

import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import uk.gov.govuk.data.auth.AuthRepo

class AuthenticationViewModelTest {
    private val authRepo = mockk<AuthRepo>(relaxed = true)

    @Test
    fun `Given user session is active, then authentication state should be logged in`() {
        every { authRepo.isUserSessionActive() } returns true

        val viewModel = AuthenticationViewModel(authRepo)
        val state = viewModel.authenticationState

        assert(state == AuthenticationState.LoggedIn)
    }

    @Test
    fun `Given user session is not active, then authentication state should be not logged in`() {
        every { authRepo.isUserSessionActive() } returns false

        val viewModel = AuthenticationViewModel(authRepo)
        val state = viewModel.authenticationState

        assert(state == AuthenticationState.NotLoggedIn)
    }
}
