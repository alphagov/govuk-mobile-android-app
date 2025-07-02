package uk.gov.govuk.config.data

import com.google.gson.Gson
import uk.gov.govuk.config.SignatureValidator
import uk.gov.govuk.config.data.remote.ConfigApi
import uk.gov.govuk.config.data.remote.model.Config
import uk.gov.govuk.config.data.remote.model.ConfigResponse
import uk.gov.govuk.data.model.Result.*
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi,
    private val gson: Gson,
    private val signatureValidator: SignatureValidator
) {
    private lateinit var _config: Config
    val config: Config
        get() {
            if (::_config.isInitialized) {
                return _config
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
}
