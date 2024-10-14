package uk.govuk.app.topics

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val topicsRepo: TopicsRepo
): ViewModel() {

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

    fun onTopicSelectedChanged(ref: String, isSelected: Boolean) {
        viewModelScope.launch {
            if (isSelected) {
                topicsRepo.selectTopic(ref)
            } else {
                topicsRepo.deselectTopic(ref)
            }
        }
    }
}