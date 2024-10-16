package uk.govuk.app.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicUi
import uk.govuk.app.topics.ui.model.TopicUi
import javax.inject.Inject

internal data class TopicsWidgetUiState(
    val topics: List<TopicUi>,
    val displayShowAll: Boolean
)

@HiltViewModel
internal class TopicsWidgetViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<TopicsWidgetUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            topicsRepo.topics.collect { topics ->
                val filteredTopics = topics
                    .filter { it.isSelected }
                    .map{ topicItem -> topicItem.toTopicUi() }

                _uiState.value = TopicsWidgetUiState(
                    topics = filteredTopics,
                    displayShowAll = topics.size > filteredTopics.size
                )
            }
        }
    }
}