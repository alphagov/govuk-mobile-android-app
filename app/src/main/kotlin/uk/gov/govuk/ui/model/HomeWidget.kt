package uk.gov.govuk.ui.model

enum class HomeWidget(val serializedName: String) {
    NOTIFICATIONS("notifications"),
    FEEDBACK_PROMPT("feedback_prompt"),
    SEARCH("search"),
    RECENT_ACTIVITY("recent_activity"),
    TOPICS("topics"),
    LOCAL("local")
}
