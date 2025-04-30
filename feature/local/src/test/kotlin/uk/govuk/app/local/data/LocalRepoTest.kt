package uk.govuk.app.local.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.govuk.app.local.data.local.LocalDataSource
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.LocalAuthorityResponse

@RunWith(Enclosed::class)
class LocalRepoTest {
    class GetLocalPostcodeTest {
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val localDataSource = mockk<LocalDataSource>(relaxed = true)
        private val apiResponse = mockk<Response<LocalAuthorityResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform postcode lookup returns an unsuccessful response`() {
            coEvery {
                localApi.getLocalPostcode("SW1")
            } returns apiResponse

            every { apiResponse.code() } returns 500
            every { apiResponse.isSuccessful } returns false

            runTest {
                val actual = localRepo.performGetLocalPostcode("SW1")
                assertTrue(actual is Result.Error)
            }

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }
    }

    class GetLocalAuthorityTest {
        private val localApi = mockk<LocalApi>(relaxed = true)
        private val localDataSource = mockk<LocalDataSource>(relaxed = true)
        private val apiResponse = mockk<Response<LocalAuthorityResponse>>()
        private lateinit var localRepo: LocalRepo

        @Before
        fun setup() {
            localRepo = LocalRepo(localApi, localDataSource)
        }

        @Test
        fun `Perform get local authority returns an unsuccessful response`() {
            coEvery {
                localApi.getLocalAuthority("slug")
            } returns apiResponse

            every { apiResponse.code() } returns 500
            every { apiResponse.isSuccessful } returns false

            runTest {
                val actual = localRepo.performGetLocalAuthority("slug")
                assertTrue(actual is Result.Error)
            }

            coVerify(exactly = 0) {
                localDataSource.insertOrReplace(any())
            }
        }
    }
}
