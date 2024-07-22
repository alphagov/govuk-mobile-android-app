package uk.govuk.app.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.govuk.app.analytics.Analytics
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "SettingsScreen"
        private const val SCREEN_ALIAS = "SETTINGS"
        private const val TITLE = "Settings"
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            alias = SCREEN_ALIAS,
            title = TITLE
        )
    }
}