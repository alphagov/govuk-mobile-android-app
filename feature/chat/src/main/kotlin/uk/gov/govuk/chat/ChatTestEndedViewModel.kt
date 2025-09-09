package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.config.data.ConfigRepo
import javax.inject.Inject

@HiltViewModel
internal class ChatTestEndedViewModel @Inject constructor(
    private val chatDataStore: ChatDataStore,
    private val analyticsClient: AnalyticsClient,
    configRepo: ConfigRepo
): ViewModel() {
    companion object {
        const val SCREEN_CLASS = "ChatTestEnded"
        const val SCREEN_NAME = "Chat Test Ended Screen"
        const val SCREEN_TITLE = "Chat Test Ended Screen"
        const val SECTION = "Chat Test Ended"

    }

    val chatUrls = configRepo.config.chatUrls

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onLinkClick(text: String, url: String) {
        analyticsClient.visitedItemClick(
            text = text,
            url = url
        )
    }

    fun onContinueClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )

        viewModelScope.launch {
            chatDataStore.clearChatOptIn()
        }
    }
}
