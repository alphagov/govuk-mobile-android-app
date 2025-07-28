package uk.gov.govuk.chat.data

import kotlinx.coroutines.delay
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.ChatResult.AwaitingAnswer
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import uk.gov.govuk.chat.data.remote.safeChatApiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi,
    private val dataStore: ChatDataStore
) {

    suspend fun getConversation(): ChatResult<Conversation>? {
        val conversationId = dataStore.conversationId()
        return if (conversationId != null) {
            safeChatApiCall { chatApi.getConversation(conversationId) }
        } else {
            null
        }
    }

    suspend fun clearConversation() {
        dataStore.clearConversation()
    }

    suspend fun askQuestion(question: String): ChatResult<AnsweredQuestion> {
        val conversationId = dataStore.conversationId()
        return if (conversationId != null) {
            updateConversation(
                conversationId = conversationId,
                question = question
            )
        } else {
            startConversation(question)
        }
    }

    private suspend fun startConversation(question: String): ChatResult<AnsweredQuestion> {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val result = safeChatApiCall { chatApi.startConversation(requestBody) }
        if (result is Success) {
            dataStore.saveConversationId(result.value.conversationId)
        }
        return result
    }

    private suspend fun updateConversation(conversationId: String, question: String): ChatResult<AnsweredQuestion> {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        return safeChatApiCall { chatApi.updateConversation(conversationId, requestBody) }
    }

    suspend fun getAnswer(
        conversationId: String,
        questionId: String,
        wait: Int = 3,
    ): ChatResult<Answer> {
        while (true) {
            delay(wait.seconds)

            val result = safeChatApiCall { chatApi.getAnswer(conversationId, questionId) }
            if (result !is AwaitingAnswer) {
                return result
            }
        }
    }
}
