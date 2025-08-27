package uk.gov.govuk.chat

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore

class DefaultChatFeatureTest {

    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private val dataStore = mockk<ChatDataStore>(relaxed = true)

    @Test
    fun `Clear clears the repo`() {
        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            feature.clear()

            coVerify { chatRepo.clearConversation() }
        }
    }

    @Test
    fun `isEnabled checks the data store to see if the user has opted in`() {
        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            feature.isEnabled()

            coVerify { dataStore.isChatOptIn() }
        }
    }
}
