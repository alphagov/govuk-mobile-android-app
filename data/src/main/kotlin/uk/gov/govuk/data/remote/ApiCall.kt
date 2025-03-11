package uk.gov.govuk.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.*
import java.net.UnknownHostException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Success(body)
        } else {
            Error()
        }
    } catch (e: Exception) {
        when (e) {
            is UnknownHostException -> DeviceOffline()
            is HttpException -> ServiceNotResponding()
            else -> Error()
        }
    }
}