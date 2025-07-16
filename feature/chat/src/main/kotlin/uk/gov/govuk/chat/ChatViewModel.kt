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
import uk.gov.govuk.chat.data.remote.ChatResult.Success
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

            chatRepo.getConversation()?.let { result ->
                when (result) {
                    is Success -> {
                        result.value.answeredQuestions.forEach { question ->
                            addChatEntry(question)
                            updateChatEntry(question.id, question.answer)
                        }
                        // Todo - handle pending questions!!!
                    }
                    else -> { } // Todo - handle error!!!
                }
            }

            _uiState.update { it.copy(loading = false) }
        }
    }

    fun onSubmit(question: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }

            when (val result = chatRepo.askQuestion(question)) {
                is Success -> {
                    addChatEntry(result.value)
                    getAnswer(
                        conversationId = result.value.conversationId,
                        questionId = result.value.id
                    )
                }
                else -> { } // Todo - handle error!!!
            }

            _uiState.update { it.copy(loading = false) }
        }
    }

    private suspend fun getAnswer(conversationId: String, questionId: String) {
        val result = chatRepo.getAnswer(
            conversationId = conversationId,
            questionId = questionId
        )
        when (result) {
            is Success -> {
                updateChatEntry(questionId, result.value)
            }
            else -> { } // Todo - handle error!!!
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