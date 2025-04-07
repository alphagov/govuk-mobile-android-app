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

suspend fun safeLocalApiCall(
    apiCall: suspend () -> Response<ApiResponse>
): Result<ApiResponse> {
    return try {
        val response = apiCall()
        val body = response.body()
        val code = response.code()

        if (response.isSuccessful && body != null) {
            Success(body)
        } else if (code == 400) {
            Success(
                ApiResponse(
                    localAuthority = null,
                    addresses = null,
                    message = "Invalid postcode"
                )
            )
        } else if (code == 404) {
            Success(
                ApiResponse(
                    localAuthority = null,
                    addresses = null,
                    message = "Postcode not found"
                )
            )
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
