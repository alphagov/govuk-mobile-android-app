package uk.govuk.app.search.data

import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.networking.domain.ServiceNotRespondingException
import uk.govuk.app.search.data.remote.AutocompleteApi
import uk.govuk.app.search.data.remote.model.AutocompleteResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AutocompleteRepo @Inject constructor(
    private val autocompleteApi: AutocompleteApi,
) {
    suspend fun performLookup(searchTerm: String): Result<AutocompleteResponse> {
        return try {
            val response = autocompleteApi.getSuggestions(searchTerm)
            return Result.success(response)
        } catch (_: java.net.UnknownHostException) {
            Result.failure(DeviceOfflineException())
        } catch (_: retrofit2.HttpException) {
            Result.failure(ServiceNotRespondingException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
