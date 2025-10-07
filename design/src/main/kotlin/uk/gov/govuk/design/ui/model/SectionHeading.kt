package uk.gov.govuk.design.ui.model

data class SectionHeadingButton(
    val title: String,
    val altText: String,
    val onClick: () -> Unit
)
