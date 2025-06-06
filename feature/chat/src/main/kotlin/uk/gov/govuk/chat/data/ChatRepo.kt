package uk.gov.govuk.chat.data

import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.ui.model.AnswerUi
import uk.gov.govuk.chat.ui.model.AnsweredQuestionUi
import uk.gov.govuk.chat.ui.model.ConversationUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi
) {
    // TODO: set and get the conversation id from the datastore
    private var _conversationId: String = BuildConfig.CHAT_CONVERSATION_ID
    private val conversationId: String
        get() = _conversationId

    suspend fun getConversation() : ConversationUi {
        try {
            // "200": Success
            val response = chatApi.getConversation(conversationId)
            println(response)

            // TODO: do I really need all this...?
            return ConversationUi(
                id = response.id,
                answeredQuestions = response.answeredQuestions.map {
                    AnsweredQuestionUi(
                        id = it.id,
                        answer = AnswerUi(
                            id = it.answer.id,
                            createdAt = it.answer.createdAt,
                            message = it.answer.message
                        ),
                        conversationId = it.conversationId,
                        createdAt = it.createdAt,
                        message = it.message
                    )
                },
                createdAt = response.createdAt
            )
        } catch (e: Exception) {
            // TODO: handle errors
            // "404": Either a conversation never existed with this id or has now expired
            // "429": Too many requests to read endpoints from an Api User or Device ID
            println(e.message)
        }

        return ConversationUi(
            id = "",
            answeredQuestions = emptyList(),
            createdAt = ""
        )
    }
}
