package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.ui.model.ConversationUi
import javax.inject.Inject

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
): ViewModel() {
    private val _conversation: MutableStateFlow<ConversationUi?> = MutableStateFlow(null)
    val conversation: StateFlow<ConversationUi?> = _conversation.asStateFlow()

    init {
        viewModelScope.launch {
            if (chatRepo.conversationId.isNotEmpty()) {
                _conversation.value = chatRepo.getConversation()
            }
        }
    }

    fun onSubmit(question: String) {
        println("onSubmit: $question")
        viewModelScope.launch {
            if (chatRepo.conversationId.isEmpty()) {
                chatRepo.startConversation(question)
            } else {
                chatRepo.updateConversation(question)
            }
        }
    }
}
