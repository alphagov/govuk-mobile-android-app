package uk.gov.govuk.config.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi,
    private val gson: Gson,
    private val signatureValidator: SignatureValidator,
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    companion object {
        private const val CHAT_POLL_INTERVAL_FALLBACK = 3.0
    }

    private var _config: Config? = null
    val config: Config
        get() = _config ?: error("You must init config successfully before use!!!")

    suspend fun initConfig(): uk.gov.govuk.data.model.Result<Unit> = coroutineScope {
        val firebaseJob = launch {
            try {
                // Fetch and activate so values are ready immediately
                firebaseRemoteConfig.fetchAndActivate().await()
            } catch (e: Exception) {
                // Log error if needed but don't crash initConfig
                false
            }
        }

        try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.let {
                    val signature = response.headers()["x-amz-meta-govuk-sig"] ?: ""
                    val valid = signatureValidator.isValidSignature(signature, it)
                    if (!valid) {
                        return@coroutineScope InvalidSignature()
                    }

                    val configResponse = gson.fromJson(it, ConfigResponse::class.java)

                    _config = configResponse.config

                    firebaseJob.join()
                    Success(Unit)
                } ?: Error()
            } else {
                Error()
            }
        } catch (_: UnknownHostException) {
            DeviceOffline()
        } catch (_: Exception) {
            Error()
        }
    }

    val chatPollIntervalSeconds: Double
        get() = (config.chatPollIntervalSeconds?.takeIf { it > 0.0 } ?: CHAT_POLL_INTERVAL_FALLBACK)

    val localServicesHeader: String
        get() = firebaseRemoteConfig.getString("local_services_header")
}
