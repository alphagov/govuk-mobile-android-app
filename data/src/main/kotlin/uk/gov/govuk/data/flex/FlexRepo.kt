package uk.gov.govuk.data.flex

import uk.gov.govuk.data.flex.remote.FlexApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlexRepo @Inject constructor(
    private val flexApi: FlexApi
) {
    suspend fun getUserId(accessToken: String): String? {
        val response = flexApi.getFlexPreferences("Bearer $accessToken")
        return response.body()?.userId
    }
}
