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
internal class ChatOptInViewModel @Inject constructor(
    private val chatDataStore: ChatDataStore,
    private val analyticsClient: AnalyticsClient,
    configRepo: ConfigRepo
): ViewModel() {
    companion object {
        const val SCREEN_CLASS = "ChatOptIn"
        const val SCREEN_NAME = "Chat Opt In Screen"
        const val SCREEN_TITLE = "Chat Opt In Screen"
        const val SECTION = "Chat Opt In"
        const val ACTION = "Opt In/Out Button Click"
    }

    val chatUrls = configRepo.config.chatUrls

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onButtonClick(text: String) {
        analyticsClient.buttonFunction(
            text = text,
            section = SECTION,
            action = ACTION
        )
    }

    fun onOptInClicked() {
        viewModelScope.launch {
            saveChatOptIn()
        }
    }

    fun onOptOutClicked() {
        viewModelScope.launch {
            saveChatOptOut()
        }
    }

    fun clearOptIn() {
        viewModelScope.launch {
            clearChatOptIn()
        }
    }

    private suspend fun saveChatOptIn() {
        chatDataStore.saveChatOptIn()
    }

    private suspend fun saveChatOptOut() {
        chatDataStore.saveChatOptOut()
    }

    private suspend fun clearChatOptIn() {
        chatDataStore.clearChatOptIn()
    }
}
