package uk.gov.govuk.chat.data.remote

import retrofit2.Response
import uk.gov.govuk.chat.data.remote.ChatResult.AuthError
import uk.gov.govuk.chat.data.remote.ChatResult.AwaitingAnswer
import uk.gov.govuk.chat.data.remote.ChatResult.DeviceOffline
import uk.gov.govuk.chat.data.remote.ChatResult.Error
import uk.gov.govuk.chat.data.remote.ChatResult.NotFound
import uk.gov.govuk.chat.data.remote.ChatResult.RateLimitExceeded
import uk.gov.govuk.chat.data.remote.ChatResult.Success
import uk.gov.govuk.chat.data.remote.ChatResult.ValidationError

internal suspend fun <T> safeChatApiCall(apiCall: suspend () -> Response<T>): ChatResult<T> {
    return try {
        val response = apiCall()
        val body = response.body()
        val code = response.code()

        when {
            response.isSuccessful -> {
                when {
                    code == 202 -> AwaitingAnswer()
                    body != null -> Success(body)
                    else -> Error()
                }
            }
            else -> {
                when (code) {
                    401 -> AuthError()
                    403 -> AuthError()
                    404 -> NotFound()
                    422 -> ValidationError()
                    429 -> RateLimitExceeded()
                    else -> Error()
                }
            }
        }
    } catch (e: Exception) {
        DeviceOffline()
    }
}