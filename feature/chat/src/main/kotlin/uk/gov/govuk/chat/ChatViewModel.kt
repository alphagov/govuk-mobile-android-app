package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.ui.model.ChatEntry
import javax.inject.Inject
import javax.inject.Named

internal data class ChatUiState(
    val chatEntries: Map<String, ChatEntry> = emptyMap(),
    val loading: Boolean = false
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo,
    @Named("main") dispatcher: CoroutineDispatcher
): ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(
        ChatUiState(
            chatEntries = emptyMap(),
            loading = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(loading = true) }

            chatRepo.getConversation()?.let { conversation ->
                conversation.answeredQuestions.forEach { question ->
                    addChatEntry(question)
                    updateChatEntry(question.id, question.answer)
                }

                // Todo - handle pending questions!!!
            }

            _uiState.update { it.copy(loading = false) }
        }
    }

    fun onSubmit(question: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            chatRepo.askQuestion(question)?.let {
                addChatEntry(it)
                val answer = chatRepo.getAnswer(
                    conversationId = it.conversationId,
                    questionId = it.id
                )
                updateChatEntry(it.id, answer)
            }

            _uiState.update { it.copy(loading = false) }
        }
    }

    private fun addChatEntry(question: AnsweredQuestion) {
        _uiState.update {
            it.copy(
                chatEntries = it.chatEntries.plus(
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
            _uiState.value.chatEntries[questionId]?.let { chatEntry ->
                chatEntry.answer = answer.message
                chatEntry.sources = answer.sources?.map { source ->
                    "* [${source.title}](${source.url})"
                }
            }
        }
    }
}