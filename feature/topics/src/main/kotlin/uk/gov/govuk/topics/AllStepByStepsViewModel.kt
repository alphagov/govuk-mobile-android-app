package uk.gov.govuk.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.analytics.data.local.model.EcommerceEvent
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.extension.toTopicContent
import uk.gov.govuk.topics.ui.model.TopicUi.TopicContent
import uk.gov.govuk.visited.Visited
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

    private val _stepBySteps: MutableStateFlow<List<TopicContent>> = MutableStateFlow(
        topicsRepo.stepBySteps.map { it.toTopicContent() }
    )
    val stepBySteps = _stepBySteps.asStateFlow()

    fun onPageView(stepBySteps: List<TopicContent>, title: String) {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = title,
            title = title
        )

        sendViewItemListEvent(stepBySteps = stepBySteps, title = title)
    }

    fun onStepByStepClick(
        section: String,
        text: String,
        url: String,
        selectedItemIndex: Int
    ) {
        analyticsClient.buttonClick(
            text = text,
            url = url,
            external = true,
            section = section
        )

        sendSelectItemEvent(
            title = text,
            section = section,
            text = text,
            url = url,
            selectedItemIndex = selectedItemIndex
        )

        viewModelScope.launch {
            visited.visitableItemClick(title = text, url = url)
        }
    }

    private fun sendSelectItemEvent(
        section: String,
        text: String,
        title: String?,
        url: String?,
        selectedItemIndex: Int
    ) {
        analyticsClient.selectItemEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = title ?: "",
                items = listOf(
                    EcommerceEvent.Item(
                        itemName = text,
                        itemCategory = section,
                        locationId = url ?: ""
                    )
                )
            ),
            selectedItemIndex = selectedItemIndex
        )
    }

    private fun sendViewItemListEvent(
        stepBySteps: List<TopicContent>,
        title: String
    ) {
        var topicItems = listOf<EcommerceEvent.Item>()

        if (stepBySteps.isNotEmpty()) {
            val stepByStepsTitle = "Step by step guides"

            topicItems = listOf(
                stepBySteps to stepByStepsTitle,
            ).flatMap { (items, category) ->
                items.map { item ->
                    EcommerceEvent.Item(
                        itemName = item.title,
                        itemCategory = category,
                        locationId = item.url
                    )
                }
            }
        }

        analyticsClient.viewItemListEvent(
            ecommerceEvent = EcommerceEvent(
                itemListName = "Topics",
                itemListId = title,
                items = topicItems
            )
        )
    }
}
