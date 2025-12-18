package uk.gov.govuk.data.auth.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import uk.gov.govuk.data.local.DataDataStore
import uk.gov.govuk.data.local.DataRepo

class DataRepoTest {

    private val dataDataStore = mockk<DataDataStore>(relaxed = true)

    @Test
    fun `Given we have a realm key, When get realm key, then return realm key`() {
        val repo = DataRepo(dataDataStore)

        coEvery { dataDataStore.getRealmKey() } returns "12345"

        runTest {
            assertEquals("12345", repo.getRealmKey())
        }
    }

    @Test
    fun `Given we have a realm key, When save realm key, then save realm key is called`() {
        val repo = DataRepo(dataDataStore)

        runTest {
            repo.saveRealmKey("12345")

            coVerify { dataDataStore.saveRealmKey("12345") }
        }
    }

    @Test
    fun `Given we have a realm iv, When get realm iv, then return realm iv`() {
        val repo = DataRepo(dataDataStore)

        coEvery { dataDataStore.getRealmIv() } returns "12345"

        runTest {
            assertEquals("12345", repo.getRealmIv())
        }
    }

    @Test
    fun `Given we have a realm iv, When save realm iv, then save realm iv is called`() {
        val repo = DataRepo(dataDataStore)

        runTest {
            repo.saveRealmIv("12345")

            coVerify { dataDataStore.saveRealmIv("12345") }
        }
    }

    @Test
    fun `Given we have a sub id, When get sub id, then return sub id`() {
        val repo = DataRepo(dataDataStore)

        coEvery { dataDataStore.getSubId() } returns "12345"

        runTest {
            assertEquals("12345", repo.getSubId())
        }
    }

    @Test
    fun `Given we have a sub id, When save sub id, then save sub id is called`() {
        val repo = DataRepo(dataDataStore)

        runTest {
            repo.saveSubId("12345")

            coVerify { dataDataStore.saveSubId("12345") }
        }
    }
}
