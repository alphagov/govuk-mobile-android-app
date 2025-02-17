package uk.govuk.app.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.analytics.data.local.model.EcommerceEvent
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicItemUi
import uk.govuk.app.topics.ui.model.TopicItemUi
import javax.inject.Inject

internal data class TopicsWidgetUiState(
    val topics: List<TopicItemUi>,
    val isError: Boolean,
    val isCustomised: Boolean,
    val displayShowAll: Boolean
)

@HiltViewModel
internal class TopicsWidgetViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    private val _uiState: MutableStateFlow<TopicsWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                val filteredTopics = topics
                    .filter { it.isSelected }
                    .map{ topicItem -> topicItem.toTopicItemUi() }

                _uiState.value = TopicsWidgetUiState(
                    topics = filteredTopics,
                    isError = topics.isEmpty(),
                    isCustomised = topicsRepo.isTopicsCustomised(),
                    displayShowAll = topics.size > filteredTopics.size
                )
            }
        }
    }

    fun onTopicSelectClick(
        ref: String,
        title: String,
        selectedItemIndex: Int
    ) {
        sendSelectItemEvent(
            title = title,
            section = ref,
            selectedItemIndex = selectedItemIndex
        )
    }

    fun onPageView(topics: List<TopicItemUi>) {
        sendViewItemListEvent(topics)
    }

    private fun sendSelectItemEvent(
        section: String,
        title: String,
        selectedItemIndex: Int
    ) {
        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "HomeScreen",
                itemListId = "Homepage",
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = title,
                        itemCategory = "Topics",
                        locationId = section
                    )
                )
            ),
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendViewItemListEvent(topics: List<TopicItemUi>) {
        val items = mutableListOf<EcommerceEvent.Item>()

        topics.forEach { topic ->
            items += EcommerceEvent.Item(
                itemName = topic.title,
                itemCategory = "Topics",
                locationId = topic.ref
            )
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "HomeScreen",
                itemListId = "Homepage",
                items = items
            )
        )
    }
}
