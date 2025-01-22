package uk.govuk.app.data.remote

import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            Result.failure(Exception())
        }
    } catch (e: Exception) {
        when (e) {
            is UnknownHostException -> Result.failure(DeviceOfflineException())
            is HttpException -> Result.failure(ServiceNotRespondingException())
            else -> Result.failure(e)
        }
    }
}