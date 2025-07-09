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
import uk.gov.govuk.chat.data.remote.model.Source

class ChatRepoTest {
    private val chatApi = mockk<ChatApi>(relaxed = true)
    private lateinit var chatRepo: ChatRepo

    @Before
    fun setup() {
        chatRepo = ChatRepo(chatApi)
    }

    @Test
    fun `Successful start conversation returns 201`() = runTest {
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
    fun `Successful update conversation returns 201`() = runTest {
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
    fun `Successful get answer returns 200`() = runTest {
        val answer = Answer(
            id = "id",
            createdAt = "createdAt",
            message = "message",
            sources = emptyList()
        )

        coEvery { chatRepo.getAnswer(wait = 0, retries = 1) } returns answer

        assertEquals("id", answer.id)
        assertEquals("createdAt", answer.createdAt)
        assertEquals("message", answer.message)
        assertEquals(emptyList<Source>(), answer.sources)
    }

    @Test
    fun `Successful get answer returns 202`() = runTest {
//        TODO - should try again until 200 or retries limit is reached
//        TODO - when the retry limit is reached, I suggest we return a fake Answer with a suitable message
    }

    @Test
    fun `Successful get conversation returns 200`() = runTest {
        val conversation = Conversation(
            id = "id",
            answeredQuestions = emptyList(),
            createdAt = "createdAt"
        )

        coEvery { chatRepo.getConversation() } returns conversation

        assertEquals("id", conversation.id)
        assertEquals(emptyList<Source>(), conversation.answeredQuestions)
        assertEquals("createdAt", conversation.createdAt)
    }

    @Test
    fun `API call returns 400`() = runTest {
//        TODO - should show full page generic "There's a problem" message
    }

    @Test
    fun `API call returns 403`() = runTest {
//        TODO - should show full page generic "There's a problem" message
//        We should never see this - especially once the API gateway is in place
    }

    @Test
    fun `API call returns 404`() = runTest {
//        TODO - should show full page generic "There's a problem" message
    }

    @Test
    fun `API call returns 422`() = runTest {
//        TODO - should show input field error "There's a problem" message
//        I suggest we don't need this as it indicates PII or blank input,
//        both of these issues should be handled on the UI
    }

    @Test
    fun `API call returns 429`() = runTest {
//        TODO - should show full page generic "There's a problem" message
//        We should never see this - especially once the API gateway is in place
    }

    @Test
    fun `API call returns 500`() = runTest {
//        TODO - should show full page generic "There's a problem" message
    }

    @Test
    fun `API call throws UnknownHostException`() = runTest {
//        TODO - should show full page device not connected message
    }

    @Test
    fun `API call throws HttpException`() = runTest {
//        TODO - should show full page Api not responding message
    }
}
