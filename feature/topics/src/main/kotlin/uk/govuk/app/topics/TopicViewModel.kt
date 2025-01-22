package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.data.model.Result
import uk.govuk.app.data.model.Result.*
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicUi
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.navigation.TOPIC_SUBTOPIC_ARG
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

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )
    }

    fun onContentClick(
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
    }

    fun onSubtopicClick(
        text: String
    ) {
        analyticsClient.buttonClick(
            text = text,
            external = false,
            section = SUBTOPIC_SECTION
        )
    }
}
