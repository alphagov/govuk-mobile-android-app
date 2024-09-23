package uk.govuk.app.config

import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi
) {
    private lateinit var config: Config

    fun getConfig(): Config {
        if (::config.isInitialized) {
            return config
        } else {
            throw IllegalStateException("You must init config successfully before use!!!")
        }
    }

    suspend fun initConfig(): Boolean {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.let {
                    config = it.config
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