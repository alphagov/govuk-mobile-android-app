package uk.gov.govuk.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uk.gov.govuk.data.local.AppDataStore
import uk.gov.govuk.ui.model.HomeWidget

class AppRepoTest {

    private val appDataStore = mockk<AppDataStore>(relaxed = true)

    @Test
    fun `Given the user has not previously completed onboarding, When is onboarding completed, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isOnboardingCompleted() } returns false

        runTest {

            assertFalse(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed onboarding, When is onboarding completed, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isOnboardingCompleted() } returns true

        runTest {
            assertTrue(repo.isOnboardingCompleted())
        }
    }

    @Test
    fun `Given the user has completed onboarding, When onboarding completed, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.onboardingCompleted()

            coVerify { appDataStore.onboardingCompleted() }
        }
    }

    @Test
    fun `Given the user has not previously completed topic selection, When is topic selection completed, then return false`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns false

        runTest {

            assertFalse(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has previously completed topic selection, When is topic selection completed, then return true`() {
        val repo = AppRepo(appDataStore)

        coEvery { appDataStore.isTopicSelectionCompleted() } returns true

        runTest {
            assertTrue(repo.isTopicSelectionCompleted())
        }
    }

    @Test
    fun `Given the user has completed topic selection, When topic selection completed, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.topicSelectionCompleted()

            coVerify { appDataStore.topicSelectionCompleted() }
        }
    }

    @Test
    fun `Given the data store contains suppressed home widgets, When the suppressed home widgets flow is requested, then forward the flow from the data store`() {
        val suppressedWidgets = flowOf(setOf("Widget 1"))
        every { appDataStore.suppressedHomeWidgets } returns suppressedWidgets

        val repo = AppRepo(appDataStore)

        runTest {
            assertEquals(suppressedWidgets, repo.suppressedHomeWidgets)
        }
    }

    @Test
    fun `Given the user has suppressed a home widget, When suppress home widget, then update data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.suppressHomeWidget(HomeWidget.SEARCH)

            coVerify { appDataStore.suppressHomeWidget(HomeWidget.SEARCH) }
        }
    }

    @Test
    fun `Given a different user has logged in, When clear, then clear data store`() {
        val repo = AppRepo(appDataStore)

        runTest {
            repo.clear()

            coVerify { appDataStore.clear() }
        }
    }
}