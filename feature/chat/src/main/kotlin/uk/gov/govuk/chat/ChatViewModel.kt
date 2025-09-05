package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.ChatResult.AuthError
import uk.gov.govuk.chat.data.remote.ChatResult.NotFound
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.ChatResult.ValidationError
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.data.auth.AuthRepo
import javax.inject.Inject

internal data class ChatUiState(
    val question: String = "",
    val chatEntries: LinkedHashMap<String, ChatEntry> = linkedMapOf(),
    val isLoading: Boolean = false,
    val isPiiError: Boolean = false,
    val displayCharacterWarning: Boolean = false,
    val displayCharacterError: Boolean = false,
    val charactersRemaining: Int = 0,
    val isSubmitEnabled: Boolean = false,
    val isError: Boolean = false,
    val isRetryableError: Boolean = false,
    val hasSeenOnboarding: Boolean? = null
)

@HiltViewModel
internal class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepo,
    private val chatDataStore: ChatDataStore,
    private val authRepo: AuthRepo,
    private val analyticsClient: AnalyticsClient,
    configRepo: ConfigRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<ChatUiState> = MutableStateFlow(
        ChatUiState(
            chatEntries = linkedMapOf(),
            isLoading = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _authError = MutableSharedFlow<Unit>()
    val authError: SharedFlow<Unit> = _authError

    val chatUrls = configRepo.config.chatUrls

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(hasSeenOnboarding = chatDataStore.isChatIntroSeen())
            }
        }
        loadConversation()
    }

    fun loadConversation() {
        viewModelScope.launch {
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

    fun clearConversation() {
        viewModelScope.launch {
            _uiState.value = ChatUiState(isLoading = true)
            chatRepo.clearConversation()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setChatIntroSeen() {
        viewModelScope.launch {
            chatDataStore.saveChatIntroSeen()
            _uiState.update { it.copy(hasSeenOnboarding = true) }
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
                analyticsClient.chatQuestionAnswerReturnedEvent()
            }
        }
    }

    fun onQuestionUpdated(
        question: String,
        characterLimit: Int = 300,
        characterWarningThreshold: Int = 50
    ) {
        val remainingCharacters = characterLimit - question.length
        val displayCharacterError = remainingCharacters < 0

        _uiState.update {
            it.copy(
                question = question,
                isPiiError = false,
                displayCharacterWarning = remainingCharacters in 0..characterWarningThreshold,
                displayCharacterError = displayCharacterError,
                charactersRemaining = characterLimit - question.length,
                isSubmitEnabled = question.isNotBlank() && !displayCharacterError
            )
        }
    }

    fun onPageView(screenClass: String, screenName: String, title: String) {
        analyticsClient.screenView(
            screenClass = screenClass,
            screenName = screenName,
            title = title
        )
    }

    fun onActionItemClicked(text: String, section: String, action: String) {
        analyticsClient.buttonFunction(
            text = text,
            section = section,
            action = action
        )
    }

    fun onAboutClick(text: String) {
        analyticsClient.chatActionMenuAboutClick(
            text = text,
            url = chatUrls.about
        )
    }

    fun onQuestionSubmit() {
        analyticsClient.chat()
    }

    fun onButtonClicked(text: String, section: String) {
        analyticsClient.buttonClick(
            text = text,
            section = section
        )
    }

    fun onMarkdownLinkClicked(text: String, url: String) {
        analyticsClient.chatMarkdownLinkClick(text = text, url = url, )
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
            is NotFound -> _uiState.update { it.copy(isRetryableError = true) }
            is AuthError -> {
                authRepo.clear()
                _authError.emit(Unit)
            }
            else -> _uiState.update { it.copy(isError = true) }
        }
    }

    private fun addChatEntry(questionId: String, question: String) {
        _uiState.update {
            it.copy(
                chatEntries = LinkedHashMap(it.chatEntries).apply {
                    put(
                        questionId,
                        ChatEntry(
                            question = question,
                            answer = "",
                            sources = emptyList()
                        )
                    )
                }
            )
        }
    }

    private fun updateChatEntry(questionId: String, answer: Answer?) {
        if (answer != null) {
            _uiState.value.chatEntries[questionId]?.let { chatEntry ->
                chatEntry.answer = answer.message
                chatEntry.sources = answer.sources?.map { source ->
                    "[${source.title}](${source.url})"
                }
            }
        }
    }
}
