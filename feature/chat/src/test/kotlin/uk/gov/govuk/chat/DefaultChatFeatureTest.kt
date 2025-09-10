package uk.gov.govuk.chat

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultChatFeatureTest {

    private val chatRepo = mockk<ChatRepo>(relaxed = true)
    private val dataStore = mockk<ChatDataStore>(relaxed = true)
    private lateinit var feature: DefaultChatFeature

    @Before
    fun setup() {
        feature = DefaultChatFeature(chatRepo, dataStore)
    }

    @Test
    fun `Clear clears the repo`() {
        runTest {
            feature.clear()

            coVerify { chatRepo.clear() }
        }
    }

    @Test
    fun `hasOptedIn returns flow from data store`() {
        val flow = flowOf(true)

        every { dataStore.hasOptedIn } returns flow

        runTest {
            assertEquals(flow, feature.hasOptedIn())
        }
    }

    @Test
    fun `Should display opt in`() = runTest {
        coEvery { dataStore.isChatOptInNull() } returns true

        assertTrue(
            feature.shouldDisplayOptIn(
                isChatOptInEnabled = true,
                isChatTestActive = true
            )
        )
    }

    @Test
    fun `Should not display opt in - opt in is disabled`() = runTest {
        coEvery { dataStore.isChatOptInNull() } returns true

        assertFalse(
            feature.shouldDisplayOptIn(
                isChatOptInEnabled = false,
                isChatTestActive = true
            )
        )
    }

    @Test
    fun `Should not display opt in - test not active`() = runTest {
        coEvery { dataStore.isChatOptInNull() } returns true

        assertFalse(
            feature.shouldDisplayOptIn(
                isChatOptInEnabled = true,
                isChatTestActive = false
            )
        )
    }

    @Test
    fun `Should not display opt in - local opt in not null`() = runTest {
        coEvery { dataStore.isChatOptInNull() } returns false

        assertFalse(
            feature.shouldDisplayOptIn(
                isChatOptInEnabled = true,
                isChatTestActive = true
            )
        )
    }

    @Test
    fun `Should display test ended, should not clear opt in`() = runTest {
        coEvery { dataStore.hasOptedIn } returns flowOf(true)

        assertTrue(feature.shouldDisplayTestEnded(isChatTestActive = false))

        coVerify(exactly = 0) {
            dataStore.clearChatOptIn()
        }
    }

    @Test
    fun `Should not display test ended, should not clear opt in - test is active`() = runTest {
        assertFalse(feature.shouldDisplayTestEnded(isChatTestActive = true))

        coVerify(exactly = 0) {
            dataStore.clearChatOptIn()
        }
    }

    @Test
    fun `Should not display test ended, should clear opt in - empty flow`() = runTest {
        coEvery { dataStore.hasOptedIn } returns emptyFlow()

        assertFalse(feature.shouldDisplayTestEnded(isChatTestActive = false))

        coVerify {
            dataStore.clearChatOptIn()
        }
    }

    @Test
    fun `Should not display test ended, should clear opt in - flow returns false`() = runTest {
        coEvery { dataStore.hasOptedIn } returns flowOf(false)

        assertFalse(feature.shouldDisplayTestEnded(isChatTestActive = false))

        coVerify {
            dataStore.clearChatOptIn()
        }
    }
}
