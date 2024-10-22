package uk.govuk.app.topics

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analytics: Analytics,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    init {
        savedStateHandle.get<String>(TOPIC_REF_ARG)?.let { ref ->
            viewModelScope.launch {
                val topic = topicsRepo.fetchTopic(ref)
                Log.d("Blah", "$topic")
            }
        }
    }
}