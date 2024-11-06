package uk.govuk.app.topics.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicItemUi
import uk.govuk.app.topics.ui.model.TopicItemUi
import javax.inject.Inject

@HiltViewModel
internal class TopicSelectionViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analytics: Analytics
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
                _topics.value = topics.map { topicItem ->
                    // Todo - items are selected by default
                    topicItem.toTopicItemUi()
                }
            }
        }
    }

    fun onPageView(title: String) {
        /*analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = title
        )*/
    }

    fun onClick(ref: String, title: String) {
//        analytics.buttonClick(title)
        _topics.value = _topics.value?.map { topic ->
            if (topic.ref == ref) {
                topic.copy(isSelected = !topic.isSelected)
            } else {
                topic
            }
        }
    }
}