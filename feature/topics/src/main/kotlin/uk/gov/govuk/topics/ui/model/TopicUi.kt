package uk.gov.govuk.topics.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

internal data class TopicUi(
    val title: String,
    val description: String?,
    val testWebViews: List<WebViewContent>,
    val popularPages: List<TopicContent>,
    val stepBySteps: List<TopicContent>,
    val displayStepByStepSeeAll: Boolean,
    val services: List<TopicContent>,
    val subtopics: List<Subtopic>,
    val subtopicsSection: Section
) {
    data class Section(
        @StringRes val title: Int,
        @DrawableRes val icon: Int
    )

    data class Subtopic(
        val ref: String,
        val title: String
    )

    data class TopicContent(
        val url: String,
        val title: String
    )

    data class WebViewContent(
        val title: String,
        val contentItemUrl: String,
    )
}
