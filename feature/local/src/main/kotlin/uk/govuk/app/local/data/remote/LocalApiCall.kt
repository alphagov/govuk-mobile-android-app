package uk.govuk.app.local.data.remote

import retrofit2.HttpException
import retrofit2.Response
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.ServiceNotResponding
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.domain.StatusCode
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
        } else {
            if (StatusCode.isErrorStatus(code)) {
                Success(
                    ApiResponse(
                        localAuthority = null,
                        addresses = null,
                        status = code
                    )
                )
            } else {
                Error()
            }
        }
    } catch (e: Exception) {
        when (e) {
            is UnknownHostException -> DeviceOffline()
            is HttpException -> ServiceNotResponding()
            else -> Error()
        }
    }
}
