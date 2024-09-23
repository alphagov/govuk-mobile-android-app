package uk.govuk.app.config

import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi
) {

    // Todo - make it a nullable val and throw exception if null???
    private lateinit var config: Config

    fun getConfig(): Config {
        return config
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