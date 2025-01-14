package uk.govuk.app.search.data

import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.networking.domain.ServiceNotRespondingException
import uk.govuk.app.search.data.local.SearchLocalDataSource
import uk.govuk.app.search.data.remote.SearchApi
import uk.govuk.app.search.data.remote.model.SearchResponse
import uk.govuk.app.search.domain.SearchConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchRepo @Inject constructor(
    private val searchApi: SearchApi,
    private val localDataSource: SearchLocalDataSource
) {

    suspend fun fetchPreviousSearches(): List<String> {
        return localDataSource.fetchPreviousSearches().map { it.searchTerm }
    }

    suspend fun removePreviousSearch(searchTerm: String) {
        localDataSource.removePreviousSearch(searchTerm)
    }

    suspend fun performSearch(
        searchTerm: String, count: Int = SearchConfig.DEFAULT_RESULTS_PER_PAGE
    ): Result<SearchResponse> {
        localDataSource.insertOrUpdatePreviousSearch(searchTerm)

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

