package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.ui.model.ConversationUi
import javax.inject.Inject

internal data class ChatUiState(
    val conversation: ConversationUi?,
    val conversationId: String?,
    val loading: Boolean = false
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
): ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (chatRepo.conversationId.isNotEmpty()) {
                _uiState.value = ChatUiState(
                    conversation = chatRepo.getConversation(),
                    conversationId = chatRepo.conversationId,
                    loading = false
                )
            }
        }
    }

    fun onSubmit(question: String) {
        viewModelScope.launch {
            if (chatRepo.conversationId.isEmpty()) {
                _uiState.value = ChatUiState(
                    conversation = null,
                    conversationId = "",
                    loading = true
                )
                chatRepo.startConversation(question)
            } else {
                _uiState.value = _uiState.value?.copy(
                    loading = true
                )
                chatRepo.updateConversation(question)
            }

            // TODO: add proper polling for response...
            for (i in 1..6) {
                delay(3000)

                _uiState.value = ChatUiState(
                    conversation = chatRepo.getConversation(),
                    conversationId = chatRepo.conversationId,
                    loading = true
                )
            }

            _uiState.value = ChatUiState(
                conversation = chatRepo.getConversation(),
                conversationId = chatRepo.conversationId,
                loading = false
            )
        }
    }
}
