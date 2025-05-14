package uk.govuk.app.local

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority

@OptIn(ExperimentalCoroutinesApi::class)
class LocalSelectViewModelTest {
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)
    private val localRepo = mockk<LocalRepo>(relaxed = true)
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LocalSelectViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        viewModel = LocalSelectViewModel(analyticsClient, localRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given a select by Local Authority page view, then log analytics`() {
        viewModel.onSelectByLocalAuthorityPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalAuthoritySelectScreen",
                screenName = "Local Select",
                title = "Local Select"
            )
        }
    }

    @Test
    fun `Given a select by Address page view, then log analytics`() {
        viewModel.onSelectByAddressPageView()

        verify {
            analyticsClient.screenView(
                screenClass = "LocalAddressSelectScreen",
                screenName = "Local Select",
                title = "Local Select"
            )
        }
    }

    @Test
    fun `Given a select by Address button click, then log analytics`() {
        viewModel.onSelectByAddressButtonClick("button text")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )
        }
    }

    @Test
    fun `Given slug selection, then log analytics`() {
        viewModel.updateLocalAuthority("button text", "dorset")

        coVerify {
            analyticsClient.buttonClick(
                text = "button text",
                section = "Local"
            )

            localRepo.updateLocalAuthority("dorset")
        }
    }

    @Test
    fun `Given local authorities, then sort by name`() {
        val remoteLocalAuthorityZ = RemoteLocalAuthority("Z Authority", "url1", "unitary", "slug-one")
        val remoteLocalAuthorityA = RemoteLocalAuthority("A Authority", "url2", "unitary", "slug-two")

        coEvery {
            localRepo.localAuthorityList
        } returns listOf(remoteLocalAuthorityZ, remoteLocalAuthorityA)

        val actual = viewModel.localAuthorities()

        assert(actual[0] == remoteLocalAuthorityA)
        assert(actual[1] == remoteLocalAuthorityZ)
    }


    @Test
    fun `Given addresses, then sort by address`() {
        val addressZ = Address("Z Test Street, AB1C1DE", "slug-one", "Slug One")
        val addressA = Address("A Test Street, AB1C1DE", "slug-two", "Slug Two")

        coEvery {
            localRepo.addressList
        } returns listOf(addressZ, addressA)

        val actual = viewModel.addresses()

        assert(actual[0] == addressA)
        assert(actual[1] == addressZ)
    }
}
