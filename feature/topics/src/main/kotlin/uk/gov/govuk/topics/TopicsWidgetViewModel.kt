package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicItemUi
import uk.gov.govuk.topics.ui.model.TopicItemUi
import javax.inject.Inject

internal data class TopicsWidgetUiState(
    val allTopics: List<TopicItemUi>,
    val yourTopics: List<TopicItemUi>,
    val isError: Boolean
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
                val mappedTopics = topics
                    .map{ topicItem -> topicItem.toTopicItemUi() }

                _uiState.value = TopicsWidgetUiState(
                    allTopics = mappedTopics,
                    yourTopics = mappedTopics.filter { it.isSelected },
                    isError = topics.isEmpty()
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
