package uk.gov.govuk.chat

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var chatRepo: ChatRepo
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        chatRepo = mockk<ChatRepo>(relaxed = true)
        viewModel = ChatViewModel(chatRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Initial Conversation State is null from ViewModel`() = runTest {
        coEvery { chatRepo.conversationId } returns ""

        println(chatRepo.conversationId)
        println(viewModel.conversation.value)

        assertEquals(null, viewModel.conversation.value)
    }

//    TODO: Fix these tests - they fail
//    @Test
//    fun `Initial Conversation State has existing value from ViewModel`() = runTest {
//        coEvery { chatRepo.conversationId } returns "id"
//
//        assertEquals("id", viewModel.conversation.value)
//    }

//    @Test
//    fun `Successful conversation start when conversationId is empty`() = runTest {
//        coEvery { chatRepo.conversationId } returns ""
//
//        val question = "question"
//        viewModel.onSubmit(question)
//
//        coVerify(exactly = 1) { chatRepo.startConversation(question) }
//        coVerify(exactly = 0) { chatRepo.updateConversation(question) }
//    }
//
//    @Test
//    fun `Successful conversation update when conversationId is not empty`() = runTest {
//        coEvery { chatRepo.conversationId } returns "id"
//
//        val question = "question"
//        viewModel.onSubmit(question)
//
//        coVerify(exactly = 0) { chatRepo.startConversation(question) }
//        coVerify(exactly = 1) { chatRepo.updateConversation(question) }
//    }
}
