package uk.govuk.app.config

import okhttp3.Headers
import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi
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

    suspend fun initConfig(): Boolean {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                val headers: Headers = response.headers()
                val signature = headers.get("x-amz-meta-govuk-sig")
                println("signature: $signature")
                // Todo - validate signature
                response.body()?.let {
                    _config = it.config
                    true
                } ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
