package uk.gov.govuk.config.data

import com.google.gson.Gson
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.config.data.remote.model.EmergencyBanner
import uk.gov.govuk.config.data.remote.model.EmergencyBannerType
import uk.gov.govuk.config.data.remote.model.Link
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
    private val signatureValidator: SignatureValidator
) {

    companion object {
        private const val CHAT_POLL_INTERVAL_FALLBACK = 3.0
    }

    private lateinit var _config: Config

    val emergencyBanners: List<EmergencyBanner> = listOf(
        // notable Death
        EmergencyBanner(
            id = "bridges-001",
            title = "Notable Death",
            body = "It has been announced that a notable figure has passed away.",
            link = Link(
                title = "Read official announcement",
                url = "https://www.gov.uk/"
            ),
            type = EmergencyBannerType.NOTABLE_DEATH,
            allowsDismissal = true
        ),

        // national Emergency
        EmergencyBanner(
            id = "nat-emergency-flood-2025",
            title = "National Emergency Declared",
            body = "Severe flooding is impacting several regions. Stay indoors and avoid travel.",
            link = null,
            type = EmergencyBannerType.NATIONAL_EMERGENCY,
            allowsDismissal = true
        ),

        // local Emergency
        EmergencyBanner(
            id = "local-m25-closure",
            title = null,
            body = "Junctions 10-12 of the M25 are closed due to an incident.",
            link = Link(
                title = "Check live traffic",
                url = "https://www.gov.uk/"
            ),
            type = EmergencyBannerType.LOCAL_EMERGENCY,
            allowsDismissal = true
        ),

        // info
        EmergencyBanner(
            id = "info-bank-holiday-004",
            title = "Bank Holiday Hours",
            body = "Services will be operating on a reduced schedule this Monday.",
            link = Link(
                title = "See opening times",
                url = "https://www.gov.uk/"
            ),
            type = EmergencyBannerType.INFORMATION,
            allowsDismissal = true
        )
    )

    val config: Config
        get() {
            if (::_config.isInitialized) {
                return _config.also { it.emergencyBanners = emergencyBanners }
            } else {
                error("You must init config successfully before use!!!")
            }
        }

    suspend fun initConfig(): uk.gov.govuk.data.model.Result<Unit> {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.let {
                    val signature = response.headers()["x-amz-meta-govuk-sig"] ?: ""
                    val valid = signatureValidator.isValidSignature(signature, it)
                    if (!valid) {
                        return InvalidSignature()
                    }

                    val configResponse = gson.fromJson(it, ConfigResponse::class.java)

                    _config = configResponse.config
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
}
