package uk.gov.govuk.design.ui.model

sealed interface ListItemStyle {
    data object Default : ListItemStyle
    data object Icon : ListItemStyle
    data class Removable(
        val altText: String,
        val onRemoveClick: () -> Unit
    ) : ListItemStyle
}
