package uk.gov.govuk.data.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.ExperimentalEphemeralBrowsing
import androidx.core.content.edit
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.authentication.AuthenticatorPromptConfiguration
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.ERROR
import uk.gov.govuk.data.auth.AuthRepo.RefreshStatus.SUCCESS
import uk.gov.govuk.data.auth.model.Tokens
import uk.gov.govuk.data.remote.AuthApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class AuthRepo @Inject constructor(
    private val attestationProvider: AttestationProvider,
    private val authRequest: AuthorizationRequest,
    private val authService: AuthorizationService,
    private val tokenRequestBuilder: TokenRequest.Builder,
    private val tokenResponseMapper: TokenResponseMapper,
    private val secureStore: SecureStore,
    private val biometricManager: BiometricManager,
    private val sharedPreferences: SharedPreferences,
    private val authApi: AuthApi,
    private val analyticsClient: AnalyticsClient
) {
    companion object {
        private const val REFRESH_TOKEN_KEY = "refreshToken"
        private const val SUB_ID_KEY = "subId"
    }

    @ExperimentalEphemeralBrowsing
    val authIntent: Intent by lazy {
        val intent = CustomTabsIntent.Builder()
            .setEphemeralBrowsingEnabled(true)
            .build()
        authService.getAuthorizationRequestIntent(authRequest, intent)
    }

    private var tokens = Tokens()

    suspend fun handleAuthResponse(data: Intent?): Boolean {
        clear()
        val authResponse = data?.let { AuthorizationResponse.fromIntent(it) }
        return if (authResponse != null) {
            performTokenRequest(authResponse.createTokenExchangeRequest())
        } else {
            false
        }
    }

    enum class RefreshStatus {
        LOADING, SUCCESS, ERROR
    }

    fun refreshTokens(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): Flow<RefreshStatus> = flow {
        val result = retrieveRefreshToken(
            activity = activity,
            title = title,
            subtitle = subtitle,
            description = description
        )

        if (result is RetrievalEvent.Success) {
            val refreshToken = result.value[REFRESH_TOKEN_KEY]
            if (refreshToken.isNullOrBlank()) {
                secureStore.delete(REFRESH_TOKEN_KEY)
                analyticsClient.logException(IllegalArgumentException("refresh token is null or blank"))
                emit(ERROR)
            } else {
                emit(RefreshStatus.LOADING)
                emit(if (refreshTokens(refreshToken)) SUCCESS else ERROR)
            }
        } else {
            emit(ERROR)
        }
    }

    suspend fun refreshTokens(): Boolean = refreshTokens(tokens.refreshToken)

    private suspend fun refreshTokens(refreshToken: String): Boolean {
        val tokenRequest = tokenRequestBuilder
            .setRefreshToken(refreshToken)
            .build()

        return performTokenRequest(tokenRequest, refreshToken)
    }

    private suspend fun performTokenRequest(
        tokenRequest: TokenRequest,
        refreshToken: String? = null
    ): Boolean {
        val attestationToken = attestationProvider.getToken()
        val clientAuth = object: ClientAuthentication {
            override fun getRequestHeaders(clientId: String): MutableMap<String, String> =
                attestationToken?.let {
                    mutableMapOf("X-Attestation-Token" to it)
                } ?: mutableMapOf()

            override fun getRequestParameters(clientId: String): MutableMap<String, String> =
                mutableMapOf(
                    "client_id" to clientId,
                    "scope" to "openid%20email"
                )
        }

        return suspendCoroutine { continuation ->
            authService.performTokenRequest(
                tokenRequest,
                clientAuth
            ) { tokenResponse, exception ->
                val success = handleTokenResponse(tokenResponse, exception, refreshToken)
                continuation.resume(success)
            }
        }
    }

    private fun handleTokenResponse(
        tokenResponse: TokenResponse?,
        exception: AuthorizationException?,
        refreshToken: String? = null
    ): Boolean {
        val mappedTokenResponse = tokenResponseMapper.map(tokenResponse)
        val accessToken = mappedTokenResponse.accessToken
        val idToken = mappedTokenResponse.idToken
        val mappedRefreshToken = refreshToken ?: mappedTokenResponse.refreshToken

        return if (exception == null &&
            !accessToken.isNullOrBlank() &&
            !idToken.isNullOrBlank() &&
            !mappedRefreshToken.isNullOrBlank()
        ) {
            tokens = Tokens(
                accessToken = accessToken,
                idToken = idToken,
                refreshToken = mappedRefreshToken
            )

            true
        } else {
            if (exception?.type == AuthorizationException.TYPE_OAUTH_TOKEN_ERROR) {
                secureStore.delete(REFRESH_TOKEN_KEY)
            }

            false
        }
    }

    suspend fun persistRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): Boolean {
        secureStore.upsert(REFRESH_TOKEN_KEY, tokens.refreshToken)
        val result = retrieveRefreshToken(
            activity = activity,
            title = title,
            subtitle = subtitle,
            description = description
        )

        return if (result is RetrievalEvent.Success) {
            true
        } else {
            secureStore.delete(REFRESH_TOKEN_KEY)
            false
        }
    }

    private suspend fun retrieveRefreshToken(
        activity: FragmentActivity,
        title: String,
        subtitle: String? = null,
        description: String? = null
    ): RetrievalEvent {
        return secureStore.retrieveWithAuthentication(
            key = arrayOf(REFRESH_TOKEN_KEY),
            authPromptConfig = AuthenticatorPromptConfiguration(
                title = title,
                subTitle = subtitle,
                description = description
            ),
            context = activity
        )
    }

    fun isAuthenticationEnabled(androidVersion: Int = Build.VERSION.SDK_INT): Boolean {
        val authenticators = if (androidVersion > Build.VERSION_CODES.Q) {
            // DEVICE_CREDENTIAL only supported after Android 29
            Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
        } else {
            Authenticators.BIOMETRIC_STRONG
        }
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun isUserSessionActive(): Boolean {
        return tokens.accessToken.isNotBlank()
    }

    fun getAccessToken(): String {
        return tokens.accessToken
    }

    fun isUserSignedIn(): Boolean {
        return secureStore.exists(REFRESH_TOKEN_KEY)
    }

    fun isDifferentUser(): Boolean {
        val currentSubId = sharedPreferences.getString(SUB_ID_KEY, "")
        val newSubId = getIdTokenProperty("sub")
        sharedPreferences.edit(commit = true) { putString(SUB_ID_KEY, newSubId) }

        return currentSubId != "" && currentSubId != newSubId
    }

    fun getUserEmail(): String {
        return getIdTokenProperty("email")
    }

    fun getIdTokenIssuedAtDate(): Long? {
        return getIdTokenProperty("iat").toLongOrNull()
    }

    private fun getIdTokenProperty(name: String): String {
        val parts = tokens.idToken.split(".")
        return try {
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
                val json = JSONObject(payload)
                return json.getString(name)
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun clear(): Boolean {
        try {
            secureStore.delete(REFRESH_TOKEN_KEY)

            try {
                if (tokens.refreshToken.isNotBlank()) {
                    authApi.revoke(
                        refreshToken = tokens.refreshToken,
                        clientId = BuildConfig.AUTH_CLIENT_ID
                    )
                }
            } catch (e: Exception) {
                // Ignore API failure
            }

            tokens = Tokens()
            return true
        } catch (e: SecureStorageError) {
            return false
        }
    }

    fun endUserSession() {
        tokens = Tokens()
    }
}
