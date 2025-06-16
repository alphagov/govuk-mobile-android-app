package uk.gov.govuk.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject


@HiltViewModel
internal class LoginSuccessViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LoginSuccessScreen"
        private const val SCREEN_NAME = "Login Success"
        private const val TITLE = "Login Success"
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
            section = LOGIN_SECTION
        )
    }
}
