package uk.gov.govuk.chat.data

import kotlinx.coroutines.delay
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi
) {
    // TODO: set and get the conversation id from the datastore
    private var _conversationId: String = ""
    val conversationId: String
        get() = _conversationId

    private var _questionId: String = ""
    val questionId: String
        get() = _questionId

    suspend fun startConversation(question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val response = chatApi.startConversation(requestBody)
        _conversationId = response.body()?.conversationId ?: ""
        _questionId = response.body()?.id ?: ""

        return response.body()
    }

    suspend fun updateConversation(question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val response = chatApi.updateConversation(conversationId, requestBody)
        _questionId = response.body()?.id ?: ""

        return response.body()
    }

    suspend fun getConversation(): Conversation? {
        val response = chatApi.getConversation(conversationId)

        return response.body()
    }

    suspend fun getAnswer(): Answer? {
        val wait = 4
        val retries = 12
        var counter = 0

        while (counter < retries) {
            delay(wait.seconds)

            val response = chatApi.getAnswer(conversationId, questionId)
            if (response.code() == 200) {
                return response.body()
            }

            counter++
        }
        return null
    }
}
