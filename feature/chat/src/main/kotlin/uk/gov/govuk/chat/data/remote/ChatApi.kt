package uk.gov.govuk.chat.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest

interface ChatApi {
    @Headers(
        "content-type: application/json",
        "authorization: Bearer ${BuildConfig.CHAT_TOKEN}"
    )
    @POST("conversation")
    suspend fun startConversation(
        @Body requestBody: ConversationQuestionRequest
    ): Response<AnsweredQuestion>

    @Headers(
        "content-type: application/json",
        "authorization: Bearer ${BuildConfig.CHAT_TOKEN}"
    )
    @GET("conversation/{conversationId}")
    suspend fun getConversation(
        @Path("conversationId") conversationId: String
    ): Response<Conversation>

    @Headers(
        "content-type: application/json",
        "authorization: Bearer ${BuildConfig.CHAT_TOKEN}"
    )
    @PUT("conversation/{conversationId}")
    suspend fun updateConversation(
        @Path("conversationId") conversationId: String,
        @Body requestBody: ConversationQuestionRequest
    ): Response<AnsweredQuestion>

    @Headers(
        "content-type: application/json",
        "authorization: Bearer ${BuildConfig.CHAT_TOKEN}"
    )
    @GET("conversation/{conversationId}/questions/{questionId}/answer")
    suspend fun getAnswer(
        @Path("conversationId") conversationId: String,
        @Path("questionId") questionId: String
    ): Response<Answer>
}
