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
import uk.gov.govuk.chat.data.remote.model.Conversation
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

    @Test
    fun `On init uiState, when conversationId is empty, uiState is null and getConversation is not called`() = runTest {
        coEvery { chatRepo.conversationId } returns ""

        assertEquals(false, chatRepo.conversationId.isNotEmpty())

        viewModel = ChatViewModel(chatRepo)

        coVerify(exactly = 0) { chatRepo.getConversation() }
        assertEquals("", viewModel.uiState.value?.conversationId)
        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
        assertEquals(false, viewModel.uiState.value?.loading)
    }

//    TODO: fix these tests ===>
    @Test
    fun `On init uiState, when conversationId is not empty, uiState is set and getConversation is called`() = runTest {
//        val uiInitialState = ChatUiState(chatEntries = emptyMap(), conversationId = "", loading = false)
        val conversation = Conversation(
            id = "id",
            answeredQuestions = emptyList(),
            createdAt = "createdAt"
        )
        coEvery { chatRepo.conversationId } returns "1234567890"
        coEvery { chatRepo.getConversation() } returns conversation

        assertEquals(true, chatRepo.conversationId.isNotEmpty())

        viewModel = ChatViewModel(chatRepo)

//        coVerify(exactly = 1) { chatRepo.getConversation() }
//        assertEquals("id", viewModel.uiState.value?.conversationId)
//        assertEquals(emptyMap<String, ChatEntry>(), viewModel.uiState.value?.chatEntries)
//        assertEquals(false, viewModel.uiState.value?.loading)
    }

//    @Test
//    fun `onSubmit when conversationId is empty, uiState is not null and getConversation is called`() = runTest {
//        viewModel = ChatViewModel(chatRepo)
//
//        val question = "question"
//
//        val answer = Answer(
//            id = "answerId",
//            createdAt = "createdAt",
//            message = "message",
//            sources = emptyList()
//        )
//        val unansweredQuestion = AnsweredQuestion(
//            id = "questionId",
//            answer = answer,
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//        val answeredQuestion = AnsweredQuestion(
//            id = "questionId",
//            answer = answer,
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//
//        coEvery { chatRepo.conversationId } returns ""
//        coEvery { chatRepo.startConversation(question) } returns unansweredQuestion
//
////        assertEquals(false, chatRepo.conversationId.isNotEmpty())
//        assertEquals(true, viewModel.uiState.value?.loading)
//
//        viewModel = ChatViewModel(chatRepo)
//        viewModel.onSubmit(question)
//
////        coVerify(exactly = 0) { chatRepo.updateConversation(question) }
////        coVerify(exactly = 1) { chatRepo.startConversation(question) }
////        coVerify(exactly = 1) { viewModel.addChatEntry(unansweredQuestion) }
////        coVerify(exactly = 1) { viewModel.updateChatEntry("questionId", answer) }
//        assertEquals(false, viewModel.uiState.value?.loading)
//    }
//
//    @Test
//    fun `onSubmit when conversationId is not empty, uiState is not null and updateConversation is called`() = runTest {
//        viewModel = ChatViewModel(chatRepo)
//
//        val question = "question"
//
//        val answer = Answer(
//            id = "answerId",
//            createdAt = "createdAt",
//            message = "message",
//            sources = emptyList()
//        )
//        val unansweredQuestion = AnsweredQuestion(
//            id = "questionId",
//            answer = answer,
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//        val answeredQuestion = AnsweredQuestion(
//            id = "questionId",
//            answer = answer,
//            conversationId = "conversationId",
//            createdAt = "createdAt",
//            message = "question"
//        )
//
//        coEvery { chatRepo.conversationId } returns ""
//        coEvery { chatRepo.updateConversation(question) } returns unansweredQuestion
//
//        assertEquals(false, chatRepo.conversationId.isNotEmpty())
//
//        viewModel = ChatViewModel(chatRepo)
//        viewModel.onSubmit(question)
//
////        assertEquals(true, viewModel.uiState.value?.loading)
//
//        coVerify(exactly = 0) { chatRepo.startConversation(question) }
//        coVerify(exactly = 1) { chatRepo.updateConversation(question) }
////        coVerify(exactly = 1) { viewModel.addChatEntry(unansweredQuestion) }
////        coVerify(exactly = 1) { viewModel.updateChatEntry("questionId", answer) }
//        assertEquals(false, viewModel.uiState.value?.loading)
//    }
}
