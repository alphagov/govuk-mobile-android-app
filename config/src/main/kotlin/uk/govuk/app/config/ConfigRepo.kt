package uk.govuk.app.config

import uk.govuk.app.config.data.remote.ConfigApi
import uk.govuk.app.config.data.remote.model.Config
import javax.inject.Inject

class ConfigRepo @Inject constructor(
    private val configApi: ConfigApi
) {

    suspend fun getConfig(): Config? {
        return try {
            val response = configApi.getConfig()
            if (response.isSuccessful) {
                response.body()?.config // Todo - handle empty body
            } else {
                // Todo - handle error status codes etc
                null
            }
        } catch (e: Exception) {
            // Todo - handle exception
            null
        }
    }

}