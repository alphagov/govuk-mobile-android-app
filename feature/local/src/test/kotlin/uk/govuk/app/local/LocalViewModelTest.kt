package uk.govuk.app.local

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.LocalAuthority

@RunWith(Enclosed::class)
@OptIn(ExperimentalCoroutinesApi::class)
class LocalViewModelTest {
    class AnalyticsTest {
        private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
        private val localRepo = mockk<LocalRepo>(relaxed = true)
        private val dispatcher = UnconfinedTestDispatcher()

        private lateinit var viewModel: LocalViewModel

        @Before
        fun setup() {
            Dispatchers.setMain(dispatcher)
            viewModel = LocalViewModel(analyticsClient, localRepo)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `Given a page view, then log analytics`() {
            viewModel.onPageView()

            verify {
                analyticsClient.screenView(
                    screenClass = "LocalScreen",
                    screenName = "Local",
                    title = "Local"
                )
            }
        }

        @Test
        fun `Given a edit page view, then log analytics`() {
            viewModel.onEditPageView()

            verify {
                analyticsClient.screenView(
                    screenClass = "LocalEntryScreen",
                    screenName = "Local",
                    title = "Local"
                )
            }
        }
    }

    class UiStateTest {
        private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
        private val localRepo = mockk<LocalRepo>(relaxed = true)
        private val dispatcher = UnconfinedTestDispatcher()
        private val unitaryLocalAuthority = LocalAuthority(
            name = "name",
            homePageUrl = "homePageUrl",
            tier = "unitary",
            slug = "slug"
        )
        private val twoTierLocalAuthority = LocalAuthority(
            name = "name",
            homePageUrl = "homePageUrl",
            tier = "district",
            slug = "slug",
            parent = LocalAuthority(
                name = "parent name",
                homePageUrl = "parentHomePageUrl",
                tier = "county",
                slug = "slug"
            )
        )
        private val responseWithUnitaryResult = ApiResponse(
            localAuthority = unitaryLocalAuthority,
            addresses = listOf(),
            message = null
        )
        private val responseWithTwoTierResult = ApiResponse(
            localAuthority = twoTierLocalAuthority,
            addresses = listOf(),
            message = null
        )
        private val addresses = listOf(
            Address(
                address = "address1",
                slug = "slug1",
                name = "name1"
            ),
            Address(
                address = "address2",
                slug = "slug2",
                name = "name2"
            )
        )
        private val responseWithAddressResult = ApiResponse(
            localAuthority = null,
            addresses = addresses,
            message = null
        )

        @Before
        fun setup() {
            Dispatchers.setMain(dispatcher)
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `Given a postcode lookup that returns a unitary local authority, then we get a unitary local authority`() {
            val postcode = "E18QS"

            coEvery {
                localRepo.performGetLocalPostcode(postcode)
            } returns Success(responseWithUnitaryResult)

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchPostcode(postcode)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(postcode, uiState.postcode)
                assertEquals(unitaryLocalAuthority, uiState.localAuthority)
            }
        }

        @Test
        fun `Given a postcode lookup that returns a two-tier local authority, then we get a two-tier local authority`() {
            val postcode = "E18QS"

            coEvery {
                localRepo.performGetLocalPostcode(postcode)
            } returns Success(responseWithTwoTierResult)

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchPostcode(postcode)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(postcode, uiState.postcode)
                assertEquals(twoTierLocalAuthority, uiState.localAuthority)
            }
        }

        @Test
        fun `Given a postcode lookup that returns an ambiguous response, then we get an address list`() {
            val postcode = "E18QS"

            coEvery {
                localRepo.performGetLocalPostcode(postcode)
            } returns Success(responseWithAddressResult)

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchPostcode(postcode)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(postcode, uiState.postcode)
                assertEquals(addresses, uiState.addresses)
            }
        }

        @Test
        fun `Given a local authority lookup that returns a unitary local authority, then get a unitary local authority`() {
            val slug = "slug"

            coEvery {
                localRepo.performGetLocalAuthority(slug)
            } returns Success(responseWithUnitaryResult)

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchLocalAuthority(slug)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(slug, uiState.slug)
                assertEquals(unitaryLocalAuthority, uiState.localAuthority)
            }
        }

        @Test
        fun `Given a local authority lookup that returns a two-tier local authority, then get a two-tier local authority`() {
            val slug = "slug"

            coEvery {
                localRepo.performGetLocalAuthority(slug)
            } returns Success(responseWithTwoTierResult)

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchLocalAuthority(slug)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(slug, uiState.slug)
                assertEquals(twoTierLocalAuthority, uiState.localAuthority)
            }
        }

        @Test
        fun `Given a local authority lookup that returns a not found response, then get a message`() {
            val postcode = "SW1A1AA"
            val message = "Postcode not found"

            coEvery {
                localRepo.performGetLocalPostcode(postcode)
            } returns Success(
                ApiResponse(
                    localAuthority = null,
                    addresses = null,
                    message = message
                )
            )

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchPostcode(postcode)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(postcode, uiState.postcode)
                assertEquals(message, uiState.message)
            }
        }

        @Test
        fun `Given a local authority lookup that returns a invalid postcode response, then get a message`() {
            val postcode = "SW1"
            val message = "Invalid postcode"

            coEvery {
                localRepo.performGetLocalPostcode(postcode)
            } returns Success(
                ApiResponse(
                    localAuthority = null,
                    addresses = null,
                    message = message
                )
            )

            val viewModel = LocalViewModel(analyticsClient, localRepo)
            viewModel.onSearchPostcode(postcode)

            runTest {
                val uiState = viewModel.uiState.value

                assertEquals(postcode, uiState.postcode)
                assertEquals(message, uiState.message)
            }
        }
    }
}
