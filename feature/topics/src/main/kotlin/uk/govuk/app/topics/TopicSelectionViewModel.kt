package uk.govuk.app.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicItemUi
import uk.govuk.app.topics.ui.model.TopicItemUi
import javax.inject.Inject

internal data class TopicSelectionUiState(
    val topics: List<TopicItemUi>,
    val isDoneEnabled: Boolean
)

@HiltViewModel
internal class TopicSelectionViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "TopicSelectionScreen"
        private const val SCREEN_NAME = "Topic Selection"
    }

    private val _uiState: MutableStateFlow<TopicSelectionUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val selectedTopicRefs = mutableListOf<String>()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                _uiState.value = TopicSelectionUiState(
                    topics = topics.map { topicItem ->
                        topicItem.toTopicItemUi().copy(
                            isSelected = selectedTopicRefs.contains(topicItem.ref)
                        )
                    },
                    isDoneEnabled = selectedTopicRefs.isNotEmpty()
                )
            }
        }
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = title
        )
    }

    fun onClick(ref: String, title: String) {
        val action = if (selectedTopicRefs.contains(ref)) {
            selectedTopicRefs.remove(ref)
            ANALYTICS_TOGGLE_FUNCTION_ACTION_DESELECTED
        } else {
            selectedTopicRefs.add(ref)
            ANALYTICS_TOGGLE_FUNCTION_ACTION_SELECTED
        }

        analyticsClient.buttonFunction(
            text = title,
            section = ANALYTICS_TOGGLE_FUNCTION_SECTION,
            action = action
        )

        _uiState.value?.let {
            val topics = it.topics.map { topic ->
                topic.copy(isSelected = selectedTopicRefs.contains(topic.ref))
            }

            _uiState.value = it.copy(
                topics = topics,
                isDoneEnabled = selectedTopicRefs.isNotEmpty()
            )
        }
    }

    fun onDone(text: String) {
        analyticsClient.buttonClick(text)
        analyticsClient.topicsCustomised()
        viewModelScope.launch {
            topicsRepo.topicsCustomised()
            uiState.value?.topics?.let { topics ->
                val topicsToDeselect = topics.filter { !selectedTopicRefs.contains(it.ref) }.map { it.ref }
                topicsRepo.deselectAll(topicsToDeselect)
            }
        }
    }

    fun onSkip(text: String) {
        analyticsClient.buttonClick(text)
    }

}