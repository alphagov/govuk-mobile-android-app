package uk.gov.govuk.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.govuk.analytics.AnalyticsClient
import javax.inject.Inject


@HiltViewModel
internal class ErrorViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient
) : ViewModel() {
    companion object {
        private const val SCREEN_CLASS = "LoginErrorScreen"
        private const val SCREEN_NAME = "Login Error"
        private const val TITLE = "Login Error"
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onBack(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = LOGIN_SECTION
        )
    }
}
