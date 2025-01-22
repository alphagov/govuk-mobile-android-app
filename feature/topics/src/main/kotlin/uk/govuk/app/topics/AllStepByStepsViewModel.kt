package uk.govuk.app.topics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.data.model.Result.Success
import uk.govuk.app.topics.data.TopicsRepo
import uk.govuk.app.topics.extension.toAllStepBySteps
import uk.govuk.app.topics.navigation.TOPIC_REF_ARG
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent
import uk.govuk.app.visited.Visited
import javax.inject.Inject

@HiltViewModel
internal class AllStepByStepsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo,
    private val analyticsClient: AnalyticsClient,
    private val visited: Visited,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "AllStepByStepsScreen"
    }

    private val _stepBySteps: MutableStateFlow<List<TopicContent>?> = MutableStateFlow(null)
    val stepBySteps = _stepBySteps.asStateFlow()

    init {
        savedStateHandle.get<String>(TOPIC_REF_ARG)?.let { ref ->
            viewModelScope.launch {
                val result = topicsRepo.getTopic(ref)
                when (result) {
                    is Success -> {
                        _stepBySteps.value = result.value.toAllStepBySteps()
                    }
                    else -> { } // Todo - should probably handle failure
                }
            }
        }
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
