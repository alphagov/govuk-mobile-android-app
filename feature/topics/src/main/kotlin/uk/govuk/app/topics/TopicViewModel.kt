package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.analytics.data.local.model.EcommerceEvent
import uk.govuk.app.data.model.Result.DeviceOffline
import uk.govuk.app.data.model.Result.Success
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicUi
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.govuk.app.topics.ui.model.TopicUi
import uk.govuk.app.visited.Visited
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
                        TopicUiState.Default(result.value.toTopicUi(MAX_STEP_BY_STEPS, isSubtopic))
                    }
                    is DeviceOffline -> TopicUiState.Offline(ref)
                    else -> TopicUiState.ServiceError(ref)
                }
            }
        }
    }

    fun onPageView(
        topicUi: TopicUi?,
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
        url: String
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
            url = url
        )

        viewModelScope.launch {
            visited.visitableItemClick(title = text, url = url)
        }
    }

    fun onSeeAllClick(
        section: String,
        text: String,
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
            url = null
        )
    }

    fun onSubtopicClick(
        text: String
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
            url = null
        )
    }

    private fun sendSelectItemEvent(
        section: String,
        text: String,
        title: String?,
        url: String?
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
            )
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
        val popularPagesTitle = "Popular pages in this topic"
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
