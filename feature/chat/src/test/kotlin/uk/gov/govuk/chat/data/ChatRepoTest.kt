package uk.gov.govuk.chat.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation

class ChatRepoTest {

    private val chatApi = mockk<ChatApi>(relaxed = true)
    private val dataStore = mockk<ChatDataStore>(relaxed = true)
    private val conversationResponse = mockk<Response<Conversation>>(relaxed = true)
    private val conversation = mockk<Conversation>(relaxed = true)
    private val questionResponse = mockk<Response<AnsweredQuestion>>(relaxed = true)
    private val answeredQuestion = mockk<AnsweredQuestion>(relaxed = true)
    private val answerResponse = mockk<Response<Answer>>(relaxed = true)
    private val answer = mockk<Answer>(relaxed = true)

    private lateinit var chatRepo: ChatRepo

    @Before
    fun setup() {
        chatRepo = ChatRepo(chatApi, dataStore)
    }

    @Test
    fun `Get conversation returns null when there is no conversation id`() = runTest {
        coEvery { dataStore.conversationId() } returns null

        assertNull(chatRepo.getConversation())
    }

    @Test
    fun `Get conversation returns null when api call is not successful`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"
        coEvery { chatApi.getConversation(any()) } returns conversationResponse
        every { conversationResponse.isSuccessful } returns false

        assertNull(chatRepo.getConversation())
    }

    @Test
    fun `Get conversation returns conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"
        coEvery { chatApi.getConversation(any()) } returns conversationResponse
        every { conversationResponse.isSuccessful } returns true
        every { conversationResponse.body() } returns conversation

        assertEquals(conversation, chatRepo.getConversation())
    }

    @Test
    fun `Ask question returns null when api call is not successful after starting a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns null
        coEvery { chatApi.startConversation(any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns false

        assertNull(chatRepo.askQuestion("abc"))
    }

    @Test
    fun `Ask question returns null when response body is null after starting a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns null
        coEvery { chatApi.startConversation(any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns true
        every { questionResponse.body() } returns null

        assertNull(chatRepo.askQuestion("abc"))
    }

    @Test
    fun `Ask question returns answered question and persists conversation id after starting a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns null
        coEvery { chatApi.startConversation(any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns true
        every { questionResponse.body() } returns answeredQuestion
        every { answeredQuestion.conversationId } returns "123"

        assertEquals(answeredQuestion, chatRepo.askQuestion("abc"))

        coVerify {
            dataStore.saveConversationId("123")
        }
    }

    @Test
    fun `Ask question returns null when api call is not successful after updating a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"
        coEvery { chatApi.updateConversation("123", any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns false

        assertNull(chatRepo.askQuestion("abc"))
    }

    @Test
    fun `Ask question returns null when response body is null after updating a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"
        coEvery { chatApi.updateConversation("123", any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns true
        every { questionResponse.body() } returns null

        assertNull(chatRepo.askQuestion("abc"))
    }

    @Test
    fun `Ask question returns answered question after updating a conversation`() = runTest {
        coEvery { dataStore.conversationId() } returns "123"
        coEvery { chatApi.updateConversation("123", any()) } returns questionResponse
        every { questionResponse.isSuccessful } returns true
        every { questionResponse.body() } returns answeredQuestion

        assertEquals(answeredQuestion, chatRepo.askQuestion("abc"))
    }

    @Test
    fun `Get answer retries and returns null when api does not return an answer`() = runTest {
        coEvery { chatApi.getAnswer(any(), any()) } returns answerResponse
        every { answerResponse.isSuccessful } returns false andThen true
        every { answerResponse.code() } returns 204

        val answer = chatRepo.getAnswer("123", "abc", wait = 1, retries = 2)

        assertNull(answer)

        coVerify(exactly = 2) {
            chatApi.getAnswer("123", "abc")
        }
    }

    @Test
    fun `Get answer retries and returns answer when api does returns an answer`() = runTest {
        coEvery { chatApi.getAnswer(any(), any()) } returns answerResponse
        every { answerResponse.isSuccessful } returns false andThen true
        every { answerResponse.code() } returns 204 andThen 204 andThen 204 andThen 204 andThen 200 andThen 200
        every { answerResponse.body() } returns answer

        assertEquals(answer, chatRepo.getAnswer("123", "abc", wait = 1))

        coVerify(exactly = 3) {
            chatApi.getAnswer("123", "abc")
        }
    }
}