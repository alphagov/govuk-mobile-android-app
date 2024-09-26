package uk.govuk.app.search.api

import uk.govuk.app.search.api_result.SearchResponse
import uk.govuk.app.search.domain.ResultStatus
import uk.govuk.app.search.domain.SearchConfig
import uk.govuk.app.search.domain.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepo @Inject constructor(
    private val searchApi: SearchApi
) {

    suspend fun performSearch(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): SearchResult {
        var resultStatus: ResultStatus
        var response = SearchResponse(total = 0, results = emptyList())

        try {
            response = searchApi.getSearchResults(searchTerm, count)

            resultStatus = if (response.total == 0) {
                ResultStatus.Empty
            } else {
                ResultStatus.Success
            }
        } catch (deviceOfflineException: java.net.UnknownHostException) {
            resultStatus = ResultStatus.DeviceOffline
        } catch (serviceNotRespondingException: retrofit2.HttpException) {
            resultStatus = ResultStatus.ServiceNotResponding
        } catch (e: Exception) {
            resultStatus = ResultStatus.Error(e.message ?: "Unknown error")
        }

        return SearchResult(resultStatus, response)
    }
}
