package uk.gov.govuk.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.openid.appauth.AuthorizationResponse
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.login.data.LoginRepo
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginRepo: LoginRepo,
    private val analyticsClient: AnalyticsClient,
    val authIntent: Intent,
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LoginScreen"
        private const val SCREEN_NAME = "Login"
        private const val TITLE = "Login"

        private const val SECTION = "Login"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onContinue(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onAuthResponse(authResponse: AuthorizationResponse?) {
        Log.d("Blah", "$authResponse")
    }
}