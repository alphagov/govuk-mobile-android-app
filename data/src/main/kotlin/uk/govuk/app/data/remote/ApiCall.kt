package uk.govuk.app.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.govuk.app.data.model.Result
import uk.govuk.app.data.model.Result.*
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