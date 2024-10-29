package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicUi
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.navigation.TOPIC_SUBTOPIC_ARG
import uk.govuk.app.topics.ui.model.TopicUi
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analytics: Analytics,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "TopicScreen"
        private const val SUBTOPIC_SECTION = "Sub topics"
        private const val MAX_STEP_BY_STEPS = 3
    }

    private val _topic: MutableStateFlow<TopicUi?> = MutableStateFlow(null)
    val topic = _topic.asStateFlow()

    init {
        savedStateHandle.get<String>(TOPIC_REF_ARG)?.let { ref ->
            val isSubtopic = savedStateHandle.get<Boolean>(TOPIC_SUBTOPIC_ARG) ?: false

            viewModelScope.launch {
                _topic.value = topicsRepo.getTopic(ref)?.toTopicUi(MAX_STEP_BY_STEPS, isSubtopic)
            }
        }
    }

    fun onPageView(title: String) {
        analytics.screenView(
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
        analytics.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )
    }

    fun onSeeAllClick(
        section: String,
        text: String,
    ) {
        analytics.buttonClick(
            text = text,
            external = false,
            section = section
        )
    }

    fun onSubtopicClick(
        text: String
    ) {
        analytics.buttonClick(
            text = text,
            external = false,
            section = SUBTOPIC_SECTION
        )
    }
}