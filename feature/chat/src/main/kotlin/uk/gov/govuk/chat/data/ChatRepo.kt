package uk.gov.govuk.chat.data

import kotlinx.coroutines.delay
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatApi
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

    suspend fun getConversation(): Conversation? {
        val conversationId = dataStore.conversationId()
        if (conversationId != null) {
            val result = safeChatApiCall { chatApi.getConversation(conversationId) }
            if (result is Success) {
                return result.value
            }
        }

        // Todo - handle error!!!
        return null
    }

    suspend fun askQuestion(question: String): AnsweredQuestion? {
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

    private suspend fun startConversation(question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val result = safeChatApiCall { chatApi.startConversation(requestBody) }
        return if (result is Success) {
            dataStore.saveConversationId(result.value.conversationId)
            return result.value
        } else {
            // Todo - handle error!!!
            null
        }
    }

    private suspend fun updateConversation(conversationId: String, question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val result = safeChatApiCall { chatApi.updateConversation(conversationId, requestBody) }
        return if (result is Success) {
            return result.value
        } else {
            // Todo - handle error!!!
            null
        }
    }

    //    wait and retry set as defaults, but can be overridden for testing
    suspend fun getAnswer(
        conversationId: String,
        questionId: String,
        wait: Int = 3,
    ): Answer? {
        while (true) {
            delay(wait.seconds)

            val result = safeChatApiCall { chatApi.getAnswer(conversationId, questionId) }
            if (result is Success) {
                return result.value
            } else if (result !is AwaitingAnswer) {
                // Todo - handle error!!!
                return null
            }
        }
    }
}
