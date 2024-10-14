package uk.govuk.app.topics.data.local

import io.mockk.coEvery
import io.mockk.mockk
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.govuk.app.topics.data.local.model.LocalTopicItem

class TopicsLocalDataSourceTest {

    private val realmProvider = mockk<TopicsRealmProvider>(relaxed = true)

    private lateinit var realm: Realm

    @Before
    fun setup() {
        val config = RealmConfiguration.Builder(schema = setOf(LocalTopicItem::class))
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
    fun `Given topic items in realm, when get topics, then emit selected topic items`() {
        val expected = listOf(
            LocalTopicItem().apply {
                ref = "ref1"
                isSelected = true
            },
            LocalTopicItem().apply {
                ref = "ref2"
                isSelected = false
            }
        )

        runTest {
            realm.write {
                copyToRealm(expected[0])
                copyToRealm(expected[1])
            }

            val localDataSource = TopicsLocalDataSource(realmProvider)

            val topics = localDataSource.topics.first()

            assertEquals(2, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
            assertEquals("ref2", topics[1].ref)
            assertFalse(topics[1].isSelected)
        }
    }

    @Test
    fun `Given topics to select, when select all, then select topics in realm`() {
        runTest {
            val localDataSource = TopicsLocalDataSource(realmProvider)

            localDataSource.selectAll(listOf("ref1", "ref2"))

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(2, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
            assertEquals("ref2", topics[1].ref)
            assertTrue(topics[1].isSelected)
        }
    }

    @Test
    fun `Given a new topic to select, when select, then insert topic into realm`() {
        runTest {
            val localDataSource = TopicsLocalDataSource(realmProvider)

            localDataSource.select("ref1")

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
        }
    }

    @Test
    fun `Given an existing topic to select, when select, then update topic in realm`() {
        runTest {
            realm.write {
                LocalTopicItem().apply {
                    ref = "ref1"
                    isSelected = false
                }
            }

            val localDataSource = TopicsLocalDataSource(realmProvider)

            localDataSource.select("ref1")

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertTrue(topics[0].isSelected)
        }
    }

    @Test
    fun `Given a new topic to deselect, when deselect, then insert topic into realm`() {
        runTest {
            val localDataSource = TopicsLocalDataSource(realmProvider)

            localDataSource.deselect("ref1")

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertFalse(topics[0].isSelected)
        }
    }

    @Test
    fun `Given an existing topic to deselect, when deselect, then update topic in realm`() {
        runTest {
            realm.write {
                LocalTopicItem().apply {
                    ref = "ref1"
                    isSelected = false
                }
            }

            val localDataSource = TopicsLocalDataSource(realmProvider)

            localDataSource.deselect("ref1")

            val topics = realm.query<LocalTopicItem>().find()
            assertEquals(1, topics.size)
            assertEquals("ref1", topics[0].ref)
            assertFalse(topics[0].isSelected)
        }
    }
}