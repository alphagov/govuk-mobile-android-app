package uk.gov.govuk.topics

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.domain.model.TopicItem
import uk.gov.govuk.topics.ui.model.TopicItemUi

@OptIn(ExperimentalCoroutinesApi::class)
class TopicsWidgetViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val topicsRepo = mockk<TopicsRepo>(relaxed = true)
    private val analyticsClient = mockk<AnalyticsClient>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Given topics are emitted, When init, then emit ui state`() {
        val topics = listOf(
            TopicItem(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true
            ),
            TopicItem(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = false
            )
        )

        every { topicsRepo.topics } returns flowOf(topics)
        coEvery { topicsRepo.isTopicsCustomised() } returns false

        val expected =
            TopicsWidgetUiState(
                yourTopics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    )
                ),
                allTopics = listOf(
                    TopicItemUi(
                        ref = "benefits",
                        icon = R.drawable.ic_topic_benefits,
                        title = "Benefits",
                        description = "description",
                        isSelected = true
                    ),
                    TopicItemUi(
                        ref = "care",
                        icon = R.drawable.ic_topic_care,
                        title = "Care",
                        description = "description",
                        isSelected = false
                    )
                )
            )

        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        runTest {
            assertEquals(expected, viewModel.uiState.first())
        }
    }

    @Test
    fun `Given a homepage topics widget item is clicked, then send a select item event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        viewModel.onTopicSelectClick(
            ref = "benefits",
            title = "Benefits",
            selectedItemIndex = 42
        )

        verify {
            analyticsClient.selectItemEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "HomeScreen",
                    itemListId = "Homepage",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            itemCategory = "Topics",
                            locationId = "benefits"
                        )
                    )
                ),
                selectedItemIndex = 42
            )
        }
    }

    @Test
    fun `Given topics are empty, When the homepage is viewed, Then send a view item list event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        viewModel.onPageView(emptyList())

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "HomeScreen",
                    itemListId = "Homepage",
                    items = emptyList()
                )
            )
        }
    }

    @Test
    fun `Given there are selected topics, When the homepage is viewed, Then send a view item list event`() {
        val viewModel = TopicsWidgetViewModel(topicsRepo, analyticsClient)

        val topics = listOf(
            TopicItemUi(
                ref = "benefits",
                title = "Benefits",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_benefits
            ),
            TopicItemUi(
                ref = "care",
                title = "Care",
                description = "description",
                isSelected = true,
                icon = R.drawable.ic_topic_care
            )
        )

        viewModel.onPageView(topics)

        verify {
            analyticsClient.viewItemListEvent(
                ecommerceEvent = EcommerceEvent(
                    itemListName = "HomeScreen",
                    itemListId = "Homepage",
                    items = listOf(
                        EcommerceEvent.Item(
                            itemName = "Benefits",
                            itemCategory = "Topics",
                            locationId = "benefits"
                        ),
                        EcommerceEvent.Item(
                            itemName = "Care",
                            itemCategory = "Topics",
                            locationId = "care"
                        )
                    )
                )
            )
        }
    }
}
