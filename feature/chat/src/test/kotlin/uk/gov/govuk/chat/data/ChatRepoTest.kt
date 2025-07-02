package uk.gov.govuk.chat.data

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.ConversationQuestionRequest
import uk.gov.govuk.chat.data.remote.model.Source

class ChatRepoTest {
    private val chatApi = mockk<ChatApi>(relaxed = true)
    private lateinit var chatRepo: ChatRepo

    @Before
    fun setup() {
        chatRepo = ChatRepo(chatApi)
    }

    @Test
    fun `start conversation returns 201`() = runTest {
        val question = "question"
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )
        val answeredQuestion = AnsweredQuestion(
            id = "questionId",
            answer = Answer(
                id = "answerId",
                createdAt = "createdAt",
                message = "message",
                sources = emptyList()
            ),
            conversationId = "conversationId",
            createdAt = "createdAt",
            message = "question"
        )

        coEvery { chatApi.startConversation(requestBody) } returns Response.success(answeredQuestion)
        coEvery { chatRepo.startConversation(question) } returns answeredQuestion

        assertEquals(requestBody.userQuestion, question)
        assertEquals("questionId", answeredQuestion.id)
        assertEquals("answerId", answeredQuestion.answer.id)
        assertEquals("createdAt", answeredQuestion.answer.createdAt)
        assertEquals("message", answeredQuestion.answer.message)
        assertEquals(emptyList<Source>(), answeredQuestion.answer.sources)
        assertEquals("conversationId", answeredQuestion.conversationId)
        assertEquals("createdAt", answeredQuestion.createdAt)
        assertEquals(question, answeredQuestion.message)
    }

    @Test
    fun `start conversation returns 422`() = runTest {
//        TODO
    }

    @Test
    fun `start conversation returns 429`() = runTest {
//        TODO
    }

    @Test
    fun `update conversation returns 201`() = runTest {
        val conversationId = "conversationId"
        val question = "question"
        val requestBody = ConversationQuestionRequest(
            userQuestion = question
        )
        val answeredQuestion = AnsweredQuestion(
            id = "questionId",
            answer = Answer(
                id = "answerId",
                createdAt = "createdAt",
                message = "message",
                sources = emptyList()
            ),
            conversationId = conversationId,
            createdAt = "createdAt",
            message = "question"
        )

        coEvery { chatApi.updateConversation(conversationId, requestBody) } returns Response.success(answeredQuestion)
        coEvery { chatRepo.updateConversation(question) } returns answeredQuestion

        assertEquals(requestBody.userQuestion, question)
        assertEquals("questionId", answeredQuestion.id)
        assertEquals("answerId", answeredQuestion.answer.id)
        assertEquals("createdAt", answeredQuestion.answer.createdAt)
        assertEquals("message", answeredQuestion.answer.message)
        assertEquals(emptyList<Source>(), answeredQuestion.answer.sources)
        assertEquals("conversationId", answeredQuestion.conversationId)
        assertEquals("createdAt", answeredQuestion.createdAt)
        assertEquals(question, answeredQuestion.message)
    }

    @Test
    fun `update conversation returns 422`() = runTest {
//        TODO
    }

    @Test
    fun `update conversation returns 429`() = runTest {
//        TODO
    }

    @Test
    fun `get answer returns 200`() = runTest {
        val answer = Answer(
            id = "id",
            createdAt = "createdAt",
            message = "message",
            sources = emptyList()
        )

        coEvery { chatApi.getAnswer(any(), any()) } returns Response.success(answer)
        coEvery { chatRepo.getAnswer(wait = 0, retries = 1) } returns answer

        assertEquals("id", answer.id)
        assertEquals("createdAt", answer.createdAt)
        assertEquals("message", answer.message)
        assertEquals(emptyList<Source>(), answer.sources)
    }

    @Test
    fun `get answer returns 202`() = runTest {
//        TODO
    }

    @Test
    fun `get answer returns 404`() = runTest {
//        TODO
    }

    @Test
    fun `get answer returns 429`() = runTest {
//        TODO
    }

    @Test
    fun `get conversation returns 200`() = runTest {
        val conversation = Conversation(
            id = "id",
            answeredQuestions = emptyList(),
            createdAt = "createdAt"
        )

        coEvery { chatApi.getConversation(any()) } returns Response.success(conversation)
        coEvery { chatRepo.getConversation() } returns conversation

        assertEquals("id", conversation.id)
        assertEquals(emptyList<Source>(), conversation.answeredQuestions)
        assertEquals("createdAt", conversation.createdAt)
    }

    @Test
    fun `get conversation returns 404`() = runTest {
//        TODO
    }

    @Test
    fun `get conversation returns 429`() = runTest {
//        TODO
    }
}
