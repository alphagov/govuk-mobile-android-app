package uk.govuk.app.search.api

import uk.govuk.app.search.api_result.Results
import uk.govuk.app.search.domain.ResultStatus
import uk.govuk.app.search.domain.SearchConfig
import uk.govuk.app.search.domain.SearchResult

class SearchResultsRepository {
    private val searchResultsService = SearchResultsRetrofitInstance.searchResultsService

    suspend fun getSearchResults(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): SearchResult {
        var resultStatus: ResultStatus
        var results = Results()

        try {
            results = searchResultsService.getSearchResults(searchTerm, count)

            resultStatus = if (results.total == 0) {
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

        return SearchResult(resultStatus, results)
    }
}
