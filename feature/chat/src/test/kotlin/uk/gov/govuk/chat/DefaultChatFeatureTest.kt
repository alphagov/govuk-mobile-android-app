package uk.gov.govuk.chat

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo

class DefaultChatFeatureTest {

    private val chatRepo = mockk<ChatRepo>(relaxed = true)

    @Test
    fun `Clear clears the repo`() {
        val feature = DefaultChatFeature(chatRepo)

        runTest {
            feature.clear()

            coVerify { chatRepo.clearConversation() }
        }
    }

}