package uk.govuk.app.local.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.model.ApiResponse
import java.net.UnknownHostException

suspend fun <T> safeLocalApiCall(
    apiCall: suspend () -> Response<T>,
    safeStatusCodes: IntArray = intArrayOf()
): Result<T> {
    var treatAsSuccess: IntArray = (200..299).toList().toIntArray()
    if (safeStatusCodes.isNotEmpty()) {
        treatAsSuccess = treatAsSuccess.plus(safeStatusCodes)
    }

    return try {
        val response = apiCall()
        var body = response.body()
        return if (response.code() in treatAsSuccess) {
            if (body == null) {
                Success(
                    ApiResponse.MessageResponse(
                        response.errorBody()?.string() ?: "Unknown Error"
                    )
                ) as Result<T>
            } else {
                Success(body)
            }
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
