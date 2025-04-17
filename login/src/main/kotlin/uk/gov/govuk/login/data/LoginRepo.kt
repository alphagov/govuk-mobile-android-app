package uk.gov.govuk.login.data

import uk.gov.govuk.login.data.remote.LoginApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LoginRepo @Inject constructor(
    private val loginApi: LoginApi
) {
    val token = "some-random-token"
}
