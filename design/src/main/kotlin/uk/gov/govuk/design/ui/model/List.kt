package uk.gov.govuk.design.ui.model

import androidx.annotation.DrawableRes

sealed interface ListItemStyle {
    data object Default : ListItemStyle
    data object Icon : ListItemStyle
    data class Button(
        @DrawableRes val icon: Int,
        val altText: String,
        val onClick: () -> Unit
    ) : ListItemStyle

    data class Status(
        val title: String
    ) : ListItemStyle
}
