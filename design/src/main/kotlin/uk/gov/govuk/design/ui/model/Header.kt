package uk.gov.govuk.design.ui.model

sealed interface HeaderStyle {
    data object Default : HeaderStyle
    data class ActionButton(
        val title: String,
        val onClick: () -> Unit,
        val altText: String? = null
    ) : HeaderStyle
}
