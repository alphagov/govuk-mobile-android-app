package uk.govuk.app.search.data

import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.networking.domain.ServiceNotRespondingException
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.SearchConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepo @Inject constructor(
    private val searchApi: SearchApi
) {
    suspend fun performSearch(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): Result<SearchResponse> {
        return try {
            val response = searchApi.getSearchResults(searchTerm, count)
            Result.success(response)
        } catch (e: java.net.UnknownHostException) {
            Result.failure(DeviceOfflineException())
        } catch (e: retrofit2.HttpException) {
            Result.failure(ServiceNotRespondingException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

