package uk.govuk.app.analytics

interface Analytics {

    fun screenView(screenClass: String, screenName: String, title: String)
    fun buttonClick(screenName: String, cta: String, action: String)
    fun widgetClick(screenName: String, cta: String)
    fun search(searchTerm: String)

}