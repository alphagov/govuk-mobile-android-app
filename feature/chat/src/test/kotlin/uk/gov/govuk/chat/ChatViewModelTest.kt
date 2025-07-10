package uk.gov.govuk.chat

import io.mockk.coEvery
import io.mockk.coVerify
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
import uk.gov.govuk.chat.data.remote.ChatApi
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.ui.model.ChatEntry

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var chatApi: ChatApi
    private lateinit var chatRepo: ChatRepo
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        chatApi = mockk<ChatApi>(relaxed = true)
        chatRepo = mockk<ChatRepo>(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

//    @Test
//    fun `On init uiState, when conversationId is empty, getConversation is not called`() = runTest {
//        coEvery { chatRepo.conversationId } returns ""
//
//        viewModel = ChatViewModel(chatRepo)
//
//        assertEquals(false, chatRepo.conversationId.isNotEmpty())
//        assertEquals("", chatRepo.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
//
//        coVerify(exactly = 0) { chatRepo.getConversation() }
//    }
//
//    @Test
//    fun `On init uiState, when conversationId is not empty, getConversation is called`() = runTest {
//        coEvery { chatRepo.conversationId } returns "conversationId"
//        coEvery { chatRepo.getConversation() } returns Conversation(
//            id = "conversationId",
//            answeredQuestions = emptyList(),
//            createdAt = "createdAt"
//        )
//
//        viewModel = ChatViewModel(chatRepo)
//
//        assertEquals(true, chatRepo.conversationId.isNotEmpty())
//        assertEquals("conversationId", chatRepo.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
//
//        coVerify(exactly = 1) { chatRepo.getConversation() }
//    }
//
//    @Test
//    fun `onSubmit when conversationId is empty, startConversation is called`() = runTest {
//        coEvery { chatRepo.conversationId } returns ""
//        coEvery { chatRepo.startConversation(any()) } returns AnsweredQuestion(
//            id = "questionId",
//            answer = Answer(
//                id = "answerId",
//                createdAt = "createdAt",
//                message = "message",
//                sources = emptyList()
//            ),
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//        coEvery { chatRepo.getAnswer(any(), any()) } returns Answer(
//            id = "answerId",
//            createdAt = "createdAt",
//            message = "message",
//            sources = emptyList()
//        )
//
//        viewModel = ChatViewModel(chatRepo)
//
//        assertEquals(true, chatRepo.conversationId.isEmpty())
//        assertEquals("", chatRepo.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
//
//        viewModel.onSubmit("question")
//
//        coVerify(exactly = 1) { chatRepo.startConversation("question") }
//        assertEquals("conversationId", viewModel.uiState.value?.conversationId)
//        assertEquals(
//            mapOf(
//                "questionId" to ChatEntry(
//                    question = "question",
//                    answer = "message",
//                    sources = emptyList()
//                )
//            ), viewModel.uiState.value?.chatEntries
//        )
//        assertEquals(false, viewModel.uiState.value?.loading)
//    }
//
//    @Test
//    fun `onSubmit when conversationId is not empty, updateConversation is called`() = runTest {
//        coEvery { chatRepo.conversationId } returns "conversationId"
//        coEvery { chatRepo.updateConversation(any()) } returns AnsweredQuestion(
//            id = "questionId",
//            answer = Answer(
//                id = "answerId",
//                createdAt = "createdAt",
//                message = "message",
//                sources = emptyList()
//            ),
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//        coEvery { chatRepo.getAnswer(any(), any()) } returns Answer(
//            id = "answerId",
//            createdAt = "createdAt",
//            message = "message",
//            sources = emptyList()
//        )
//
//        viewModel = ChatViewModel(chatRepo)
//
//        assertEquals(true, chatRepo.conversationId.isNotEmpty())
//        assertEquals("conversationId", chatRepo.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
//
//        viewModel.onSubmit("question")
//
//        coVerify { chatRepo.updateConversation("question") }
////        assertEquals("conversationId", viewModel.uiState.value?.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
//    }
}
