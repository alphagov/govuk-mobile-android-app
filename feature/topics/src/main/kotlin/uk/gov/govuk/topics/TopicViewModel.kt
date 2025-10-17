package uk.gov.govuk.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicUi
import uk.gov.govuk.topics.navigation.TOPIC_REF_ARG
import uk.gov.govuk.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.gov.govuk.topics.ui.model.TopicUi
import uk.gov.govuk.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "TopicScreen"
        private const val SUBTOPIC_SECTION = "Sub topics"
        // TODO: set MAX_POPULAR_PAGES to 4 - currently 3 so can
        // see the see all button in development
        private const val MAX_POPULAR_PAGES = 3
        private const val MAX_STEP_BY_STEPS = 3
    }

    private val _uiState: MutableStateFlow<TopicUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        getTopic()
    }

    internal fun getTopic() {
        savedStateHandle.get<String>(TOPIC_REF_ARG)?.let { ref ->
            val isSubtopic = savedStateHandle.get<Boolean>(TOPIC_SUBTOPIC_ARG) == true
            viewModelScope.launch {
                val result = topicsRepo.getTopic(ref)
                _uiState.value = when (result) {
                    is Success -> {
                        TopicUiState.Default(
                            result.value.toTopicUi(
                                MAX_POPULAR_PAGES,
                                MAX_STEP_BY_STEPS,
                                isSubtopic
                            )
                        )
                    }
                    is DeviceOffline -> TopicUiState.Offline(ref)
                    else -> TopicUiState.ServiceError(ref)
                }
            }
        }
    }

    fun onPageView(
        topicUi: TopicUi? = null,
        title: String
    ) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )

        if (topicUi != null) {
            sendViewItemListEvent(topicUi = topicUi, title = title)
        }
    }

    fun onContentClick(
        title: String,
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )

        sendSelectItemEvent(
            title = title,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex
        )

        viewModelScope.launch {
            visited.visitableItemClick(title = text, url = url)
        }
    }

    fun onSeeAllClick(
        section: String,
        text: String,
        selectedItemIndex: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = section
        )

        sendSelectItemEvent(
            title = text,
            section = section,
            text = text,
            url = null,
            selectedItemIndex = selectedItemIndex
        )
    }

    fun onSubtopicClick(
        text: String,
        selectedItemIndex: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = SUBTOPIC_SECTION
        )

        sendSelectItemEvent(
            title = null,
            section = SUBTOPIC_SECTION,
            text = text,
            url = null,
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendSelectItemEvent(
        section: String,
        text: String,
        title: String?,
        url: String?,
        selectedItemIndex: Int
    ) {
        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = title ?: "",
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = text,
                        itemCategory = section,
                        locationId = url ?: ""
                    )
                )
            ),
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendViewItemListEvent(
        topicUi: TopicUi,
        title: String
    ) {
        /*
         * Tried to get these from the resource strings file, but
         * that doesn't work as it's not a Composable and needs a Context.
         * Passing in the context is a memory leak.
         * So, not sure if that's possible!
         */
        val popularPagesTitle = "Popular pages"
        val stepByStepsTitle = "Step by step guides"
        val browseTitle = "Browse"
        val servicesTitle = "Services and information"

        var topicItems = listOf(
            topicUi.popularPages to popularPagesTitle,
            topicUi.stepBySteps to stepByStepsTitle,
            topicUi.services to servicesTitle,
        ).flatMap { (items, category) ->
            items.map { item ->
                EcommerceEvent.Item(
                    itemName = item.title,
                    itemCategory = category,
                    locationId = item.url
                )
            }
        }.toMutableList()

        topicUi.subtopics.forEach { subtopic ->
            topicItems += EcommerceEvent.Item(
                itemName = subtopic.title,
                itemCategory = browseTitle,
                locationId = subtopic.ref
            )
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
