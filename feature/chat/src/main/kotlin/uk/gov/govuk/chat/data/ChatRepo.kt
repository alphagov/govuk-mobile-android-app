package uk.gov.govuk.chat.data

import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import uk.gov.govuk.chat.ui.model.AnswerUi
import uk.gov.govuk.chat.ui.model.AnsweredQuestionUi
import uk.gov.govuk.chat.ui.model.ConversationUi
import uk.gov.govuk.chat.ui.model.SourceUi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi
) {
    // TODO: set and get the conversation id from the datastore
    private var _conversationId: String = ""
    val conversationId: String
        get() = _conversationId

    suspend fun startConversation(question: String): ConversationUi? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        try {
            // "200": Success
            val response = chatApi.startConversation(requestBody)
            _conversationId = response.conversationId
            println(response)
        } catch (e: Exception) {
            // TODO: handle errors
            // "422": Validation error on question submission (such as PII in question)
            // "429": Too many requests to read endpoints from an Api User or Device ID
            println(e.message)
        }

        // TODO: add polling for response...
        return getConversation()
    }

    suspend fun updateConversation(question: String): ConversationUi? {
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )

        try {
            // "201": Success
            val response = chatApi.updateConversation(conversationId, requestBody)
            println(response)
        } catch (e: Exception) {
            // TODO: handle errors
            // "422": Validation error on question submission (such as PII in question
            //            or user already has a pending question)
            // "429": Too many requests to read endpoints from an Api User or Device ID
            println(e.message)
        }

        // TODO: add polling for response...
        return getConversation()
    }

    suspend fun getConversation(): ConversationUi? {
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
                            message = it.answer.message,
                            sources = it.answer.sources.map { source ->
                                SourceUi(
                                    url = source.url,
                                    title = source.title
                                )
                            }
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

        return null
    }

    suspend fun getAnswer(questionId: String): AnswerUi? {
        try {
            // "200": Success
            // "202": The answer is still being generated
            val response = chatApi.getAnswer(conversationId, questionId)
            println(response)

            return AnswerUi(
                id = response.id,
                createdAt = response.createdAt,
                message = response.message,
                sources = response.sources.map { source ->
                    SourceUi(
                        url = source.url,
                        title = source.title
                    )
                }
            )
        } catch (e: Exception) {
            // TODO: handle errors
            // "404": Either a conversation never existed with this id or has now expired
            // "429": Too many requests to read endpoints from an Api User or Device ID
            println(e.message)
        }

        return null
    }
}
