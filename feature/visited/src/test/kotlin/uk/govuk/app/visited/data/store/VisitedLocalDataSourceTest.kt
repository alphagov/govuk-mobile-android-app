package uk.govuk.app.visited.data.store

import io.mockk.coEvery
import io.mockk.mockk
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.govuk.app.visited.data.model.VisitedItem
import java.time.LocalDate

class VisitedLocalDataSourceTest {
    private val realmProvider = mockk<VisitedRealmProvider>(relaxed = true)

    private lateinit var realm: Realm

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(VisitedItem::class))
            .inMemory() // In-memory Realm for testing
            .build()

        // Open the Realm instance
        realm = Realm.open(config)

        coEvery { realmProvider.open() } returns realm
    }

    @After
    fun tearDown() {
        realm.close()
    }

    @Test
    fun `Given visited items in realm, when get visited items, then emit all visited items`() {
        var lastVisitedMillis = LocalDate.now().toEpochDay()

        val expected = listOf(
            VisitedItem().apply {
                title = "title1"
                url = "url1"
                lastVisited = lastVisitedMillis
            },
            VisitedItem().apply {
                title = "title2"
                url = "url2"
                lastVisited = lastVisitedMillis
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            val localDataSource = VisitedLocalDataSource(realmProvider)

            val visitedItems = localDataSource.visitedItems.first()

            assertEquals(2, visitedItems.size)

            assertEquals("title1", visitedItems[0].title)
            assertEquals("url1", visitedItems[0].url)
            assertEquals(lastVisitedMillis, visitedItems[0].lastVisited)

            assertEquals("title2", visitedItems[1].title)
            assertEquals("url2", visitedItems[1].url)
            assertEquals(lastVisitedMillis, visitedItems[1].lastVisited)
        }
    }

    /*
     * TODO: this test doesn't work because we are auto-setting the lastVisited date to today.
     * Find a way of testing this correctly - some way of moving backwards and forwards in time.
        @Test
        fun `Given an existing historical visited item, when re-visited, then update the lastVisited date`() {
            val today = LocalDate.now()
            val fiveDaysAgo = today.minusDays(5)

            val visitedItem = VisitedItem().apply {
                title = "title1"
                url = "url1"
                lastVisited = fiveDaysAgo.toEpochDay()
            }

            runTest {
                realm.write {
                    copyToRealm(visitedItem)
                }

                val localDataSource = VisitedLocalDataSource(realmProvider)

                val visitedItems = localDataSource.visitedItems.first()

                assertEquals(1, visitedItems.size)
                assertEquals("title1", visitedItems[0].title)
                assertEquals("url1", visitedItems[0].url)
                assertEquals(fiveDaysAgo.toEpochDay(), visitedItems[0].lastVisited)

                localDataSource.insertOrUpdate("title1", "url1")

                assertEquals(1, visitedItems.size)
                assertEquals("title1", visitedItems[0].title)
                assertEquals("url1", visitedItems[0].url)
                assertEquals(today.toEpochDay(), visitedItems[0].lastVisited)
            }
        }
     */
}
