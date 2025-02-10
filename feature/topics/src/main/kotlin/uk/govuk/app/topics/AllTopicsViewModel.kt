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
internal class AllTopicsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "AllTopicsScreen"
        private const val SCREEN_NAME = "All Topics"
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

    fun onClick(title: String) {
        viewModelScope.launch {
            analyticsClient.buttonClick(title)
        }
    }
}