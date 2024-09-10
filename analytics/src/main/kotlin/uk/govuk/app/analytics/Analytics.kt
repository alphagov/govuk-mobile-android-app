package uk.govuk.app.analytics

interface Analytics {

    fun screenView(screenClass: String, screenName: String, title: String)
    fun pageIndicatorClick()
    fun buttonClick(text: String)
    fun tabClick(text: String)
    fun widgetClick(text: String)
    fun search(searchTerm: String)
    suspend fun isAnalyticsEnabled(): Boolean
    suspend fun enable()
    suspend fun disable()
}