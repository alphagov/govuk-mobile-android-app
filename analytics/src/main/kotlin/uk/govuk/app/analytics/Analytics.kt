package uk.govuk.app.analytics

interface Analytics {

    fun screenView(screenClass: String, screenName: String, title: String)
    fun pageIndicatorClick()
    fun buttonClick(text: String)
    fun tabClick(text: String)
    fun widgetClick(screenName: String, cta: String)
    fun search(searchTerm: String)

}