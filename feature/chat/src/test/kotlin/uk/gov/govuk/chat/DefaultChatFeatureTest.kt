package uk.gov.govuk.chat

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import kotlin.test.assertEquals

class DefaultChatFeatureTest {

    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private val dataStore = mockk<ChatDataStore>(relaxed = true)

    @Test
    fun `Clear clears the repo`() {
        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            feature.clear()

            coVerify { chatRepo.clear() }
        }
    }

    @Test
    fun `hasOptedIn returns flow from data store`() {
        val flow = flowOf(true)

        every { dataStore.hasOptedIn } returns flow

        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            assertEquals(flow, feature.hasOptedIn())
        }
    }

    @Test
    fun `userHasNotYetChosen returns true when null in data store`() {
        coEvery { dataStore.isChatOptInNull() } returns true

        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            assertTrue(feature.userHasNotYetChosen())
        }
    }

    @Test
    fun `userHasNotYetChosen returns false when not null in data store`() {
        coEvery { dataStore.isChatOptInNull() } returns false

        val feature = DefaultChatFeature(chatRepo, dataStore)

        runTest {
            assertFalse(feature.userHasNotYetChosen())
        }
    }
}
