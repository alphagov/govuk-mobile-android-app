package uk.govuk.app.search.domain

object SearchConfig {
    val BASE_URL = "https://www.gov.uk"

//    Search API V1 ===>
//    val API_BASE_URL = "https://www.gov.uk"
//    const val SEARCH_PATH = "/api/search.json"
//    const val DESCRIPTION_RESPONSE_FIELD = "description"

    //    Search API V2 ===>
    val API_BASE_URL = "https://search.publishing.service.gov.uk"
    const val SEARCH_PATH = "/v0_1/search.json"
    //    field has both changed name and is now optional!
    const val DESCRIPTION_RESPONSE_FIELD = "description_with_highlighting"

    val DEFAULT_RESULTS_PER_PAGE = 10
}
