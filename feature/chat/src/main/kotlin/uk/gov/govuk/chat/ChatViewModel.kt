package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.ui.model.ChatEntry
import javax.inject.Inject

internal data class ChatUiState(
    val chatEntries: Map<String, ChatEntry>? = emptyMap(),
    val conversationId: String?,
    val loading: Boolean = false
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo
): ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState?> = MutableStateFlow(
        ChatUiState(
            chatEntries = emptyMap(),
            conversationId = "",
            loading = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (chatRepo.conversationId.isNotEmpty()) {
                _uiState.value = ChatUiState(
                    chatEntries = emptyMap(),
                    conversationId = chatRepo.conversationId,
                    loading = true
                )

                val conversation = chatRepo.getConversation()
                if (conversation != null && conversation.answeredQuestions.isNotEmpty()) {
                    conversation.answeredQuestions.forEach { question ->
                        addChatEntry(question)
                        updateChatEntry(question.id, question.answer)
                    }
                }

                _uiState.value = _uiState.value?.copy(
                    loading = false
                )
            }
        }
    }

    fun onSubmit(question: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(
                conversationId = null,
                loading = true
            )

            if (chatRepo.conversationId.isEmpty()) {
                val question = chatRepo.startConversation(question)
                addChatEntry(question)
                val answer = chatRepo.getAnswer()
                updateChatEntry(question!!.id, answer)
            } else {
                _uiState.value = _uiState.value?.copy(
                    conversationId = chatRepo.conversationId
                )

                val question = chatRepo.updateConversation(question)
                addChatEntry(question)
                val answer = chatRepo.getAnswer()
                updateChatEntry(question!!.id, answer)
            }

            _uiState.value = _uiState.value?.copy(
                loading = false
            )
        }
    }

    private fun addChatEntry(question: AnsweredQuestion?) {
        if (question != null) {
            _uiState.value = _uiState.value?.copy(
                chatEntries = _uiState.value?.chatEntries?.plus(
                    mapOf(
                        question.id to ChatEntry(
                            question = question.message,
                            answer = "",
                            sources = emptyList()
                        )
                    )
                )
            )
        }
    }

    private fun updateChatEntry(questionId: String, answer: Answer?) {
        if (answer != null) {
            val chatEntry = _uiState.value?.chatEntries?.get(questionId)
            if (chatEntry != null) {
                chatEntry.answer = answer.message
                chatEntry.sources = answer.sources.map { source ->
                    "* [${source.title}](${source.url})"
                }
            }
        }
    }
}
