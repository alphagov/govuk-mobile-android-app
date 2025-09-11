package uk.gov.govuk.chat

import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.data.local.ChatDataStore
import uk.gov.govuk.config.data.ConfigRepo

@OptIn(ExperimentalCoroutinesApi::class)
class ChatTestEndedViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val chatDataStore = mockk<ChatDataStore>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val configRepo = mockk<ConfigRepo>(relaxed = true)
    private lateinit var viewModel: ChatTestEndedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = ChatTestEndedViewModel(chatDataStore, analyticsClient, configRepo)
    }

    @Test
    fun `Given a page view, then log analytics`() {
        val screenClass = "ChatTestEnded"
        val screenName = "Chat Test Ended Screen"
        val title = "Chat Test Ended Screen"

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
    fun `Given continue is clicked, then log analytics and clear chat opt in`() {
        val section = "Chat Test Ended"

        viewModel.onContinueClick("button")

        verify {
            analyticsClient.buttonClick(
                text = "button",
                section = section
            )
        }

        coVerify {
            chatDataStore.clearChatOptIn()
        }
    }
}