package uk.govuk.app.visited

import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import uk.govuk.app.visited.data.store.VisitedLocalDataSource

class VisitedClientTest {
    private val visitedLocalDataSource = mockk<VisitedLocalDataSource>(relaxed = true)

    @Test
    fun `Given the user clicks on a visitable item, then run the insertOrUpdate function`() {
        val visitedClient = VisitedClient(visitedLocalDataSource)

        runTest {
            visitedClient.visitableItemClick("title", "url")

            coVerify {
                visitedLocalDataSource.insertOrUpdate("title", "url", any())
            }
        }
    }
}
