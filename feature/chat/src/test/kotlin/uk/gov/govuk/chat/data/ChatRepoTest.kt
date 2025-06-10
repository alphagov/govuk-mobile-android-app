package uk.gov.govuk.chat.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest

class ChatRepoTest {
    private val chatApi = mockk<ChatApi>(relaxed = true)
    private lateinit var chatRepo: ChatRepo

    @Before
    fun setup() {
        chatRepo = ChatRepo(chatApi)
    }

    @Test
    fun `Successful start conversation`() = runTest {
        val question = "question"
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )
        val answeredQuestion = AnsweredQuestion(
            id = "id",
            answer = Answer(
                id = "id",
                createdAt = "createdAt",
                message = "message"
            ),
            conversationId = "conversationId",
            createdAt = "createdAt",
            message = "question"
        )
        val conversation = Conversation(
            id = "id",
            answeredQuestions = listOf(answeredQuestion),
            createdAt = "createdAt"
        )

        coEvery { chatApi.startConversation(requestBody) } returns answeredQuestion
        coEvery { chatRepo.getConversation() } returns conversation.toConversationUi()

        assertEquals(conversation.answeredQuestions.first().message, question)
    }

    @Test
    fun `Successful update conversation`() = runTest {
        val conversationId = "id"
        val question = "question"
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )
        val answeredQuestion = AnsweredQuestion(
            id = "id",
            answer = Answer(
                id = "id",
                createdAt = "createdAt",
                message = "message"
            ),
            conversationId = conversationId,
            createdAt = "createdAt",
            message = "question"
        )
        val conversation = Conversation(
            id = conversationId,
            answeredQuestions = listOf(answeredQuestion),
            createdAt = "createdAt"
        )

        coEvery { chatApi.updateConversation(conversationId, requestBody) } returns answeredQuestion
        coEvery { chatRepo.getConversation() } returns conversation.toConversationUi()

        assertEquals(conversation.toConversationUi().id, conversationId)
        assertEquals(conversation.answeredQuestions.first().message, question)
    }

    @Test
    fun `Successful get conversation`() = runTest {
        val conversationId = "id"
        val conversation = Conversation(
            id = conversationId,
            answeredQuestions = emptyList(),
            createdAt = "createdAt"
        )

        coEvery { chatApi.getConversation(conversationId) } returns conversation
        coEvery { chatRepo.getConversation() } returns conversation.toConversationUi()

        assertEquals(conversation.toConversationUi().id, conversationId)
    }
}
