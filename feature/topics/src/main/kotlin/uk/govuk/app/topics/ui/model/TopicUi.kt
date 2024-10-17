package uk.govuk.app.topics.ui.model

import androidx.annotation.DrawableRes

internal data class TopicUi(
    val ref: String,
    @DrawableRes val icon: Int,
    val title: String,
    val isSelected: Boolean
)
