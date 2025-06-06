package uk.gov.govuk.chat.data

import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.data.remote.ChatApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class ChatRepo @Inject constructor(
    private val chatApi: ChatApi
) {
    private var _conversationId: String = BuildConfig.CHAT_CONVERSATION_ID
    private val conversationId: String
        get() = _conversationId

    suspend fun getConversation() {
        try {
            val response = chatApi.getConversation(conversationId)
            println(response)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
