package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicContent
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent
import uk.gov.govuk.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class AllPopularPagesViewModel @Inject constructor(
    topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "AllPopularPagesScreen"
    }

    private val _popularPages: MutableStateFlow<List<TopicContent>> = MutableStateFlow(
        topicsRepo.popularPages.map { it.toTopicContent() }
    )
    val popularPages = _popularPages.asStateFlow()

    fun onPageView(popularPages: List<TopicContent>, title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )

        sendViewItemListEvent(popularPages = popularPages, title = title)
    }

    fun onPopularPagesClick(
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int
    ) {
        onClick(
            analyticsClient = analyticsClient,
            viewModelScope = viewModelScope,
            visited = visited,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendViewItemListEvent(
        popularPages: List<TopicContent>,
        title: String
    ) {
        var topicItems = listOf<EcommerceEvent.Item>()

        if (popularPages.isNotEmpty()) {
            val popularPagesTitle = "Popular pages"

            topicItems = listOf(
                popularPages to popularPagesTitle,
            ).flatMap { (items, category) ->
                items.map { item ->
                    EcommerceEvent.Item(
                        itemName = item.title,
                        itemCategory = category,
                        locationId = item.url
                    )
                }
            }
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = title,
                items = topicItems
            )
        )
    }
}
