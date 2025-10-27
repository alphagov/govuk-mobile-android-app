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
    val yourTopics: List<TopicItemUi>
)

@HiltViewModel
internal class TopicsWidgetViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val LIST_ID = "Homepage"
        private const val LIST_NAME = "HomeScreen"
        private const val ITEM_CATEGORY = "Topics"
    }

    private val _uiState: MutableStateFlow<TopicsWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                val mappedTopics = topics
                    .map{ topicItem -> topicItem.toTopicItemUi() }

                _uiState.value = TopicsWidgetUiState(
                    allTopics = mappedTopics,
                    yourTopics = mappedTopics.filter { it.isSelected }
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
                itemListName = LIST_NAME,
                itemListId = LIST_ID,
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = title,
                        itemCategory = ITEM_CATEGORY,
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
                itemCategory = ITEM_CATEGORY,
                locationId = topic.ref
            )
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = LIST_NAME,
                itemListId = LIST_ID,
                items = items
            )
        )
    }
}
