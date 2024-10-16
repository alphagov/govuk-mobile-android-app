package uk.govuk.app.topics

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicUi
import javax.inject.Inject

internal data class TopicsUiState(
    val topics: List<TopicUi>
)

internal data class TopicUi(
    val ref: String,
    @DrawableRes val icon: Int,
    val title: String,
    val isSelected: Boolean
)

@HiltViewModel
internal class TopicsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "EditTopicsScreen"
        private const val SCREEN_NAME = "Topic Selection"
        private const val TOGGLE_FUNCTION_SECTION = "Topics"
        private const val TOGGLE_FUNCTION_ACTION_SELECTED = "Add"
        private const val TOGGLE_FUNCTION_ACTION_DESELECTED = "Remove"
    }

    private val _uiState: MutableStateFlow<TopicsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                _uiState.value = TopicsUiState(topics.map { topicItem -> topicItem.toTopicUi() })
            }
        }
    }

    fun onEdit() {
        viewModelScope.launch {
            topicsRepo.selectInitialTopics()
        }
    }

    fun onPageView(title: String) {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = title
        )
    }

    fun onTopicSelectedChanged(ref: String, title: String, isSelected: Boolean) {
        viewModelScope.launch {
            if (isSelected) {
                topicsRepo.selectTopic(ref)
            } else {
                topicsRepo.deselectTopic(ref)
            }
            logTopicToggleFunction(title, isSelected)
        }
    }

    private fun logTopicToggleFunction(text: String, isSelected: Boolean) {
        val action = if (isSelected) {
            TOGGLE_FUNCTION_ACTION_SELECTED
        } else {
            TOGGLE_FUNCTION_ACTION_DESELECTED
        }

        analytics.toggleFunction(
            text = text,
            section = TOGGLE_FUNCTION_SECTION,
            action = action
        )
    }
}