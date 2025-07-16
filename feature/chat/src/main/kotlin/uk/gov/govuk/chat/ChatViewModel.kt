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
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.chat.ui.model.ChatEntry
import javax.inject.Inject
import javax.inject.Named

internal data class ChatUiState(
    val question: String = "",
    val chatEntries: Map<String, ChatEntry> = emptyMap(),
    val isLoading: Boolean = false,
    val isPiiError: Boolean = false,
    val displayCharacterWarning: Boolean = false,
    val displayCharacterError: Boolean = false,
    val charactersRemaining: Int = 0,
    val isSubmitEnabled: Boolean = false
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo,
    @Named("main") dispatcher: CoroutineDispatcher
): ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(
        ChatUiState(
            chatEntries = emptyMap(),
            isLoading = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            _uiState.update { it.copy(isLoading = true) }

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

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onSubmit(question: String) {
        val isPiiError = StringCleaner.includesPII(question)
        _uiState.update { it.copy(isPiiError = isPiiError) }

        if (!isPiiError) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                when (val result = chatRepo.askQuestion(question)) {
                    is Success -> {
                        addChatEntry(result.value)
                        onQuestionUpdated("")
                        getAnswer(
                            conversationId = result.value.conversationId,
                            questionId = result.value.id
                        )
                    }

                    else -> {} // Todo - handle error!!!
                }

                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onQuestionUpdated(question: String) {
        val characterLimit = 300
        val remainingCharacters = characterLimit - question.length
        val displayCharacterError = remainingCharacters < 0

        _uiState.update {
            it.copy(
                question = question,
                isPiiError = false,
                displayCharacterWarning = remainingCharacters in 0..50,
                displayCharacterError = displayCharacterError,
                charactersRemaining = characterLimit - question.length,
                isSubmitEnabled = question.isNotBlank() && !displayCharacterError
            )
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