package uk.gov.govuk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimeoutManagerTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var timeoutManager: TimeoutManager

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        timeoutManager = TimeoutManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Timeout is triggered after timeout interval`() {
        runTest {
            var timedOut = false

            timeoutManager.onUserInteraction(
                interactionTime = 1000,
                timeoutInterval = 10
            ) {
                timedOut = true
            }

            delay(11)

            assertTrue(timedOut)
        }
    }

    @Test
    fun `Interaction events are throttled if received in less than a second`() {
        runTest {
            var timedOut = false

            timeoutManager.onUserInteraction(
                interactionTime = 1000,
                timeoutInterval = 10
            ) {
                timedOut = true
            }

            delay(5)

            timeoutManager.onUserInteraction(
                interactionTime = 1999,
                timeoutInterval = 10
            ) {
                timedOut = true
            }

            delay(6)

            // Timeout is triggered from the first interaction, the second interaction is ignored
            // as it occurs within the throttle threshold of 1 second
            assertTrue(timedOut)
        }
    }

}