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
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.ChatResult.ValidationError
import uk.gov.govuk.chat.data.remote.model.Answer
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
    val isSubmitEnabled: Boolean = false,
    val isError: Boolean = false
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo,
    @Named("main") private val dispatcher: CoroutineDispatcher
): ViewModel() {
    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(
        ChatUiState(
            chatEntries = emptyMap(),
            isLoading = false
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        initConversation()
    }

    fun onRetry() {
        initConversation()
    }

    private fun initConversation() {
        viewModelScope.launch(dispatcher) {
            _uiState.value = ChatUiState(isLoading = true)

            chatRepo.getConversation()?.let { result ->
                handleChatResult(result) { conversation ->
                    conversation.answeredQuestions.forEach { question ->
                        addChatEntry(
                            questionId = question.id,
                            question = question.message
                        )
                        updateChatEntry(question.id, question.answer)
                    }

                    conversation.pendingQuestion?.let { pendingQuestion ->
                        addChatEntry(
                            questionId = pendingQuestion.id,
                            question = pendingQuestion.message
                        )
                        getAnswer(
                            conversationId = conversation.id,
                            questionId = pendingQuestion.id
                        )
                    }
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

                handleChatResult(chatRepo.askQuestion(question)) { answeredQuestion ->
                    addChatEntry(
                        questionId = answeredQuestion.id,
                        question = answeredQuestion.message
                    )
                    onQuestionUpdated("")
                    getAnswer(
                        conversationId = answeredQuestion.conversationId,
                        questionId = answeredQuestion.id
                    )
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
        handleChatResult(
            chatRepo.getAnswer(
                conversationId = conversationId,
                questionId = questionId
            )
        ) { answer ->
            updateChatEntry(questionId, answer)
        }
    }

    private suspend fun <T> handleChatResult(chatResult: ChatResult<T>, onSuccess: suspend (T) -> Unit) {
        when (chatResult) {
            is Success -> onSuccess(chatResult.value)
            is ValidationError -> _uiState.update { it.copy(isPiiError = true) }
            else -> _uiState.update { it.copy(isError = true) }
        }
    }

    private fun addChatEntry(questionId: String, question: String) {
        _uiState.update {
            it.copy(
                chatEntries = it.chatEntries.plus(
                    mapOf(
                        questionId to ChatEntry(
                            question = question,
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