package uk.govuk.app.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.topics.data.remote.model.TopicItem
import javax.inject.Inject

internal data class TopicsUiState(
    // Todo - should probably have loading and error states
    val topics: List<TopicItem>
)

@HiltViewModel
internal class TopicsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<TopicsUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val topics = topicsRepo.getTopics()
            // Todo - loading and error states etc
            topics?.let {
                _uiState.value = TopicsUiState(it + it + it + it + it + it + it + it + it)
            }
        }
    }

}