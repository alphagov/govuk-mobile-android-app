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

@HiltViewModel
internal class EditTopicsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "EditTopicsScreen"
        private const val SCREEN_NAME = "Topic Selection"
    }

    private val _topics: MutableStateFlow<List<TopicItemUi>?> = MutableStateFlow(null)
    val topics = _topics.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                _topics.value = topics.map { topicItem -> topicItem.toTopicItemUi() }
            }
        }
    }

    fun onPageView(title: String) {
        viewModelScope.launch {
            analyticsClient.screenView(
                screenClass = SCREEN_CLASS,
                screenName = SCREEN_NAME,
                title = title
            )
        }
    }

    fun onTopicSelectedChanged(ref: String, title: String, isSelected: Boolean) {
        viewModelScope.launch {
            topicsRepo.topicsCustomised()
            topicsRepo.toggleSelection(ref, isSelected)
            logTopicToggleFunction(title, isSelected)
        }
    }

    private fun logTopicToggleFunction(text: String, isSelected: Boolean) {
        val action = if (isSelected) {
            ANALYTICS_TOGGLE_FUNCTION_ACTION_SELECTED
        } else {
            ANALYTICS_TOGGLE_FUNCTION_ACTION_DESELECTED
        }

        viewModelScope.launch {
            analyticsClient.toggleFunction(
                text = text,
                section = ANALYTICS_TOGGLE_FUNCTION_SECTION,
                action = action
            )
        }

        analyticsClient.topicsCustomised()
    }
}