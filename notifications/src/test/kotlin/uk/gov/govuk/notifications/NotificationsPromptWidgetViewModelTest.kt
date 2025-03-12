package uk.gov.govuk.notifications

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class NotificationsPromptWidgetViewModelTest {
    private val notificationsClient = mockk<NotificationsClient>()

    private lateinit var viewModel: NotificationsPromptWidgetViewModel

    @Before
    fun setup() {
        viewModel = NotificationsPromptWidgetViewModel(notificationsClient)
    }

    @Test
    fun `Given on click, then call request permission`() {
        every { notificationsClient.requestPermission() } returns Unit

        runTest {
            viewModel.onClick()

            verify {
                notificationsClient.requestPermission()
            }
        }
    }
}
