package uk.gov.govuk.chat.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
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

    // We do not need to store this in the datastore, it's only needed
    // until an question is answered, then it's removed
    private var _questionId: String = ""
    val questionId: String
        get() = _questionId

    suspend fun startConversation(question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val response = safeChatApiCall { chatApi.startConversation(requestBody) }
        if (response.isSuccessful) {
            _conversationId = response.body()?.conversationId ?: ""
            _questionId = response.body()?.id ?: ""
            return response.body()
        }

//        Currently not considered!
//        This seems the most sensible approach to me ATM, but needs content
//        and product agreement
        return AnsweredQuestion(
            id = questionId,
            answer = Answer(
                id = "",
                createdAt = "",
                message = "",
                sources = emptyList()
            ),
            conversationId = conversationId,
            createdAt = "",
            message = "Sorry, unable to start your conversation at the moment"
        )
    }

    suspend fun updateConversation(question: String): AnsweredQuestion? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        val response = safeChatApiCall { chatApi.updateConversation(conversationId, requestBody) }
        if (response.isSuccessful) {
            _questionId = response.body()?.id ?: ""
            return response.body()
        }

//        Currently not considered!
//        This seems the most sensible approach to me ATM, but needs content
//        and product agreement
        return AnsweredQuestion(
            id = questionId,
            answer = Answer(
                id = "",
                createdAt = "",
                message = "",
                sources = emptyList()
            ),
            conversationId = conversationId,
            createdAt = "",
            message = "Sorry, unable to update your conversation at the moment"
        )
    }

    suspend fun getConversation(): Conversation? {
        val response = safeChatApiCall { chatApi.getConversation(conversationId) }
        if (response.isSuccessful) {
            return response.body()
        }

//        Currently not considered!
//        This seems the most sensible approach to me ATM, but needs content
//        and product agreement
        return Conversation(
            id = conversationId,
            answeredQuestions = emptyList(),
            createdAt = ""
        )
    }

//    wait and retry set as defaults, but can be overridden for testing
    suspend fun getAnswer(wait: Int = 4, retries: Int = 12): Answer? {
        var counter = 0

        while (counter < retries) {
            delay(wait.seconds)

            val response = safeChatApiCall { chatApi.getAnswer(conversationId, questionId) }
            if (response.isSuccessful) {
                if (response.code() == 200) {
                    return response.body()
                }
            }

            counter++
        }

//        Currently not considered!
//        This seems the most sensible approach to me ATM, but needs content
//        and product agreement
        return Answer(
            id = "",
            createdAt = "",
            message = "Sorry, unable to answer to your question at the moment",
            sources = emptyList()
        )
    }

    private suspend fun <T> safeChatApiCall(apiCall: suspend () -> Response<T>): Response<T> {
        return try {
            val response = withContext(Dispatchers.IO) { apiCall() }
            val body = response.body()
            val code = response.code()

            if (response.isSuccessful && body != null) {
                return response
            } else {
                when (code) {
                    400 -> Response.error(400, "Error: ${response.message()}".toResponseBody())
                    403 -> Response.error(403, "Error: ${response.message()}".toResponseBody())
                    404 -> Response.error(404, "Error: ${response.message()}".toResponseBody())
                    422 -> Response.error(422, "Error: ${response.message()}".toResponseBody())
                    429 -> Response.error(429, "Error: ${response.message()}".toResponseBody())
                    500 -> Response.error(500, "Error: ${response.message()}".toResponseBody())
                    else -> Response.error(500, "Error: ${response.message()}".toResponseBody())
                }
            }
        } catch (e: Exception) {
            // TODO: handle UnknownHostException and HttpException and show the appropriate full page error screens
            Response.error(500, "Error: ${e.message}".toResponseBody())
        }
    }
}
