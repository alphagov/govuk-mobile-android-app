package uk.gov.govuk.config.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.UserFeedbackBanner
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Error
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepoImpl @Inject constructor(
    private val configApi: ConfigApi,
    private val gson: Gson,
    private val signatureValidator: SignatureValidator,
    private val firebaseRemoteConfig: FirebaseRemoteConfig
): ConfigRepo {

    companion object {
        private const val CHAT_POLL_INTERVAL_FALLBACK = 3.0
    }

    private var _config: Config? = null
    private val safeConfig: Config
        get() = checkNotNull(_config) { "You must init config successfully before use!!!" }
    override suspend fun initConfig(): uk.gov.govuk.data.model.Result<Unit> = coroutineScope {
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

    override val isAvailable: Boolean
        get() = safeConfig.available

    override val chatPollIntervalSeconds: Double
        get() = (safeConfig.chatPollIntervalSeconds?.takeIf { it > 0.0 } ?: CHAT_POLL_INTERVAL_FALLBACK)

    override val minimumVersion: String
        get() = safeConfig.minimumVersion

    override val recommendedVersion: String
        get() = safeConfig.recommendedVersion


    override val isSearchEnabled: Boolean
        get() = safeConfig.releaseFlags.search

    override val isRecentActivityEnabled: Boolean
        get() = safeConfig.releaseFlags.recentActivity

    override val isTopicsEnabled: Boolean
        get() = safeConfig.releaseFlags.topics

    override val isNotificationsEnabled: Boolean
        get() = safeConfig.releaseFlags.notifications

    override val isLocalServicesEnabled: Boolean
        get() = safeConfig.releaseFlags.localServices

    override val isExternalBrowserEnabled: Boolean
        get() = safeConfig.releaseFlags.externalBrowser

    override val chatUrls: ChatUrls
        get() = safeConfig.chatUrls

    override val userFeedbackBanner: UserFeedbackBanner?
        get() = safeConfig.userFeedbackBanner

    override val refreshTokenExpirySeconds: Long?
        get() = safeConfig.refreshTokenExpirySeconds

    override val emergencyBanners: List<EmergencyBanner>?
        get() = safeConfig.emergencyBanners

    val localServicesHeader: String
        get() = firebaseRemoteConfig.getString("local_services_header")
}
