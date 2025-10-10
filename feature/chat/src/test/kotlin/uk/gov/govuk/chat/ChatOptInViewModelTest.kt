package uk.gov.govuk.chat

import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.config.data.ConfigRepo

@OptIn(ExperimentalCoroutinesApi::class)
class ChatOptInViewModelTest {
    private val chatDataStore = mockk<ChatDataStore>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: ChatOptInViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ChatOptInViewModel(chatDataStore, analyticsClient, configRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val screenClass = "ChatOptIn"
        val screenName = "Chat Opt In Screen"
        val title = "Chat Opt In Screen"

        viewModel.onPageView()

        verify {
            analyticsClient.screenView(
                screenClass = screenClass,
                screenName = screenName,
                title = title
            )
        }
    }

    @Test
    fun `When the user clicks a button, then log analytics`() {
        val text = "text"

        viewModel.onButtonClick(text = text)

        verify {
            analyticsClient.buttonFunction(
                text = text,
                section = "Chat Opt In",
                action = "Opt In/Out Button Click"
            )
        }
    }

    @Test
    fun `When the user opts in, then set the opt in flag to true`() {
        viewModel.onOptInClicked()

        coVerify(exactly = 1) { chatDataStore.saveChatOptIn() }
    }

    @Test
    fun `When the user opts out, then set the opt in flag to false`() {
        viewModel.onOptOutClicked()

        coVerify(exactly = 1) { chatDataStore.saveChatOptOut() }
    }

    @Test
    fun `When the user has opted in, and the trial has ended, then set the opt in flag is removed`() {
        viewModel.clearOptIn()

        coVerify(exactly = 1) { chatDataStore.clearChatOptIn() }
    }
}
