package uk.govuk.app.analytics

interface Analytics {

    fun screenView(screenClass: String, screenName: String, title: String)
    suspend fun isAnalyticsConsentRequired(): Boolean
    suspend fun isAnalyticsEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
    fun pageIndicatorClick()
    fun buttonClick(
        text: String,
        url: String? = null,
        external: Boolean = false,
        section: String? = null
    )
    fun tabClick(text: String)
    fun widgetClick(text: String)
    fun search(searchTerm: String)
    fun searchResultClick(text: String, url: String)
    fun visitedItemClick(text: String, url: String)
    fun settingsItemClick(text: String, url: String)
    fun toggleFunction(text: String, section: String, action: String)
    fun buttonFunction(text: String, section: String, action: String)
}
