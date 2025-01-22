package uk.govuk.app.config.data

import com.google.gson.Gson
import uk.govuk.app.config.SignatureValidator
import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import uk.govuk.app.config.data.remote.model.ConfigResponse
import uk.govuk.app.data.remote.DeviceOfflineException
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

    suspend fun initConfig(): Result<Unit> {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.let {
                    val signature = response.headers()["x-amz-meta-govuk-sig"] ?: ""
                    val valid = signatureValidator.isValidSignature(signature, it)
                    if (!valid) {
                        return Result.failure(InvalidSignatureException())
                    }

                    val configResponse = gson.fromJson(it, ConfigResponse::class.java)

                    _config = configResponse.config
                    Result.success(Unit)
                } ?: Result.failure(Exception())
            } else {
                Result.failure(Exception())
            }
        } catch (_: UnknownHostException) {
            Result.failure(DeviceOfflineException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
