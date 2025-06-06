package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import javax.inject.Inject

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
): ViewModel() {
    fun getConversation() {
        viewModelScope.launch {
            chatRepo.getConversation()
        }
    }
}
