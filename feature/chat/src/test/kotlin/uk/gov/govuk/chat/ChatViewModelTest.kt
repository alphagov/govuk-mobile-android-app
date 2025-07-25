package uk.gov.govuk.chat

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.remote.ChatResult
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.PendingQuestion
import uk.gov.govuk.chat.data.remote.model.Source

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private val conversation = mockk<Conversation>(relaxed = true)
    private val pendingQuestion = mockk<PendingQuestion>(relaxed = true)
    private val question = mockk<AnsweredQuestion>(relaxed = true)

    private val dispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        viewModel = ChatViewModel(chatRepo)

        clearAllMocks()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Load conversation does not emit conversation if there is no conversation`() = runTest {
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            null
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.loadConversation()

        advanceUntilIdle()

        assertTrue(uiStates[1].isLoading)
        assertTrue(uiStates[2].chatEntries.isEmpty())
        assertFalse(uiStates[2].isLoading)
        assertTrue(uiStates[2].chatEntries.isEmpty())

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits conversation if there is a conversation`() = runTest {
        val conversation = Conversation(
            id = "123",
            answeredQuestions =
                listOf(
                    AnsweredQuestion(
                        "abc",
                        Answer(
                            "",
                            "",
                            "Answer 1",
                            listOf(
                                Source(
                                    "url",
                                    "title"
                                )
                            )
                        ),
                        "",
                        "",
                        "Question 1"

                    )
                ),
            "",
            null
        )

        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.Success(conversation)
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.loadConversation()

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]
        val question = chatEntry?.question
        val answer = chatEntry?.answer

        assertFalse(finalState.isLoading)
        assertEquals("Question 1", question)
        assertEquals("Answer 1", answer)
        assertEquals("[title](url)", chatEntry?.sources?.first())

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation emits error`() = runTest {
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.NotFound()
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.loadConversation()

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertFalse(finalState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)
        assertTrue(finalState.isError)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Load conversation gets answer if there is a pending question`() = runTest {
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            ChatResult.Success(conversation)
        }

        every { conversation.pendingQuestion } returns pendingQuestion

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.loadConversation()

        advanceUntilIdle()

        coVerify {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `On Submit emits PII error`() = runTest {
        viewModel.onSubmit("test@email.com")

        val uiState = viewModel.uiState.value

        assertFalse(uiState.isLoading)
        assertTrue(uiState.isPiiError)

        coVerify(exactly = 0) {
            chatRepo.askQuestion(any())
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits question`() = runTest {
        val question = AnsweredQuestion(
            "abc",
            Answer(
                "",
                "",
                "",
                null
            ),
            "",
            "",
            "Question 1"
        )


        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertFalse(finalState.isLoading)
        assertEquals("Question 1", chatEntry?.question)
    }

    @Test
    fun `Submit emits answer`() = runTest {
        val question = AnsweredQuestion(
            "abc",
            Answer(
                "",
                "",
                "",
                null
            ),
            "",
            "",
            "Question 1"
        )

        val answer = Answer(
            "",
            "",
            "Answer 1",
            listOf(
                Source(
                    "url",
                    "title"
                )
            )
        )

        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        coEvery { chatRepo.getAnswer(any(), any()) } returns ChatResult.Success(answer)

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertFalse(finalState.isLoading)
        assertEquals("Question 1", chatEntry?.question)
        assertEquals("Answer 1", chatEntry?.answer)
        assertEquals("[title](url)", chatEntry?.sources?.first())
    }

    @Test
    fun `Submit emits question PII error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.ValidationError()
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertFalse(finalState.isLoading)
        assertTrue(finalState.chatEntries.isEmpty())
        assertTrue(finalState.isPiiError)
        assertTrue(finalState.isError)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits question error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Error()
        }

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertFalse(finalState.isLoading)
        assertTrue(finalState.chatEntries.isEmpty())
        assertTrue(finalState.isError)

        coVerify(exactly = 0) {
            chatRepo.getAnswer(any(), any(), any())
        }
    }

    @Test
    fun `Submit emits answer error`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            ChatResult.Success(question)
        }

        coEvery { chatRepo.getAnswer(any(), any(), any()) } returns ChatResult.Error()

        val uiStates = mutableListOf<ChatUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(uiStates)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val loadingState = uiStates[1]

        assertTrue(loadingState.isLoading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = uiStates.last()

        assertFalse(finalState.isLoading)
        assertTrue(finalState.isError)
    }

    @Test
    fun `Question updated emits no errors or warnings`() = runTest {
        viewModel.onQuestionUpdated("abc", 10, 5)

        val uiState = viewModel.uiState.value

        assertEquals("abc", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(7, uiState.charactersRemaining)
        assertTrue(uiState.isSubmitEnabled)
    }

    @Test
    fun `Question updated emits character warning`() = runTest {
        viewModel.onQuestionUpdated("abcde", 10, 5)

        val uiState = viewModel.uiState.value

        assertEquals("abcde", uiState.question)
        assertFalse(uiState.isPiiError)
        assertTrue(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(5, uiState.charactersRemaining)
        assertTrue(uiState.isSubmitEnabled)
    }

    @Test
    fun `Question updated emits character error and submit disabled`() = runTest {
        viewModel.onQuestionUpdated("abcdefghjkl", 10, 5)

        val uiState = viewModel.uiState.value

        assertEquals("abcdefghjkl", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertTrue(uiState.displayCharacterError)
        assertEquals(-1, uiState.charactersRemaining)
        assertFalse(uiState.isSubmitEnabled)
    }

    @Test
    fun `Question updated emits submit disabled when question is blank`() = runTest {
        viewModel.onQuestionUpdated(" ", 5, 3)

        val uiState = viewModel.uiState.value

        assertEquals(" ", uiState.question)
        assertFalse(uiState.isPiiError)
        assertFalse(uiState.displayCharacterWarning)
        assertFalse(uiState.displayCharacterError)
        assertEquals(4, uiState.charactersRemaining)
        assertFalse(uiState.isSubmitEnabled)
    }
}
