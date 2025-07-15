package uk.gov.govuk.chat

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
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
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.Source

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val chatRepo = mockk<ChatRepo>(relaxed = true)

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Init emits loading and conversation`() = runTest {
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
            conversation
        }

        val testDispatcher = StandardTestDispatcher(testScheduler)
        val states = mutableListOf<ChatUiState>()

        val job = launch {
            val viewModel = ChatViewModel(chatRepo, dispatcher = testDispatcher)
            viewModel.uiState
                .toList(states)
        }

        advanceUntilIdle()

        val initialState = states[0]

        assertFalse(initialState.loading)
        assertEquals(0, initialState.chatEntries.size)

        val loadingState = states[1]

        assertTrue(loadingState.loading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = states[2]
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]
        val question = chatEntry?.question
        val answer = chatEntry?.answer

        assertFalse(finalState.loading)
        assertEquals("Question 1", question)
        assertEquals("Answer 1", answer)
        assertEquals("* [title](url)", chatEntry?.sources?.first())

        job.cancel()
    }

    @Test
    fun `Init emits loading and no conversation`() = runTest {
        coEvery { chatRepo.getConversation() } coAnswers {
            delay(100)
            null
        }

        val testDispatcher = StandardTestDispatcher(testScheduler)
        val states = mutableListOf<ChatUiState>()

        val job = launch {
            val viewModel = ChatViewModel(chatRepo, dispatcher = testDispatcher)
            viewModel.uiState
                .toList(states)
        }

        advanceUntilIdle()

        val initialState = states[0]

        assertFalse(initialState.loading)
        assertEquals(0, initialState.chatEntries.size)

        val loadingState = states[1]

        assertTrue(loadingState.loading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = states[2]

        assertFalse(finalState.loading)
        assertEquals(0, finalState.chatEntries.size)

        job.cancel()
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
            question
        }

        val states = mutableListOf<ChatUiState>()

        val viewModel = ChatViewModel(chatRepo, Dispatchers.Main)

        val job = launch {
            viewModel.uiState
                .toList(states)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val initialState = states[0]

        assertFalse(initialState.loading)
        assertEquals(0, initialState.chatEntries.size)

        val loadingState = states[1]

        assertTrue(loadingState.loading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = states[2]
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertFalse(finalState.loading)
        assertEquals("Question 1", chatEntry?.question)

        job.cancel()
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

        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            question
        }

        coEvery { chatRepo.getAnswer(any(), any()) } returns
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
                )

        val states = mutableListOf<ChatUiState>()

        val viewModel = ChatViewModel(chatRepo, Dispatchers.Main)

        val job = launch {
            viewModel.uiState
                .toList(states)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val initialState = states[0]

        assertFalse(initialState.loading)
        assertEquals(0, initialState.chatEntries.size)

        val loadingState = states[1]

        assertTrue(loadingState.loading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = states[2]
        val chatEntries = finalState.chatEntries
        val chatEntry = chatEntries["abc"]

        assertFalse(finalState.loading)
        assertEquals("Question 1", chatEntry?.question)
        assertEquals("Answer 1", chatEntry?.answer)
        assertEquals("* [title](url)", chatEntry?.sources?.first())

        job.cancel()
    }

    @Test
    fun `Submit does not emit question when repo returns null`() = runTest {
        coEvery { chatRepo.askQuestion(any()) } coAnswers {
            delay(100)
            null
        }

        val states = mutableListOf<ChatUiState>()

        val viewModel = ChatViewModel(chatRepo, Dispatchers.Main)

        val job = launch {
            viewModel.uiState
                .toList(states)
        }

        viewModel.onSubmit("First Question")

        advanceUntilIdle()

        val initialState = states[0]

        assertFalse(initialState.loading)
        assertEquals(0, initialState.chatEntries.size)

        val loadingState = states[1]

        assertTrue(loadingState.loading)
        assertEquals(0, loadingState.chatEntries.size)

        val finalState = states[2]

        assertFalse(finalState.loading)
        assertEquals(0, finalState.chatEntries.size)

        job.cancel()
    }
}
