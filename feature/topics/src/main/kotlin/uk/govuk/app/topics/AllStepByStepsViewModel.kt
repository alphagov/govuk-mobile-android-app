package uk.govuk.app.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toTopicContent
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent
import uk.govuk.app.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class AllStepByStepsViewModel @Inject constructor(
    topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "AllStepByStepsScreen"
    }

    private val _stepBySteps: MutableStateFlow<List<TopicContent>?> = MutableStateFlow(null)
    val stepBySteps = _stepBySteps.asStateFlow()

    init {
        _stepBySteps.value = topicsRepo.getStepBySteps().map { it.toTopicContent() }
    }

    fun onPageView(title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )
    }

    fun onStepByStepClick(
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
}
