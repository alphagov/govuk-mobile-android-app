package uk.gov.govuk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.remote.model.Conversation
import javax.inject.Inject

internal data class ChatUiState(
    val conversation: Conversation?,
    val conversationId: String?,
    val questionId: String?,
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
//            if (chatRepo.conversationId.isNotEmpty()) {
//                _uiState.value = ChatUiState(
//                    conversation = chatRepo.getConversation(),
//                    conversationId = chatRepo.conversationId,
//                    questionId = chatRepo.questionId,
//                    loading = false
//                )
//            }
        }
    }

    fun onSubmit(question: String) {
        viewModelScope.launch {
//            val question1 = chatRepo.startConversation("Can fred@example.com keep micropigs?")
            val question1 = chatRepo.startConversation("Can I keep micropigs?")
            println("===> Question : ${question1!!.message}")
            val answer1 = chatRepo.getAnswer()
            println("===> Answer : ${answer1!!.message}")
            println("===> Sources :")
            answer1!!.sources.forEach { source ->
                println("    * ${source.title} [${source.url}]")
            }

            val question2 = chatRepo.updateConversation("Where can I keep them?")
            println("===> Question : ${question2!!.message}")
            val answer2 = chatRepo.getAnswer()
            println("===> Answer : ${answer2!!.message}")
            println("===> Sources :")
            answer2!!.sources.forEach { source ->
                println("    * ${source.title} [${source.url}]")
            }

            val conversation = chatRepo.getConversation()
            println("===> Conversation : $conversation")

//            if (chatRepo.conversationId.isEmpty()) {
//                _uiState.value = ChatUiState(
//                    conversation = null,
//                    conversationId = "",
//                    questionId = "",
//                    loading = true
//                )
//                chatRepo.startConversation(question)
//            } else {
//                _uiState.value = _uiState.value?.copy(
//                    loading = true
//                )
//                chatRepo.updateConversation(question)
//            }
//
//            val conversation = chatRepo.getAnswer()
//
//            _uiState.value = ChatUiState(
//                conversation = conversation,
//                conversationId = chatRepo.conversationId,
//                questionId = "",
//                loading = false
//            )
        }
    }
}
