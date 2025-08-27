package uk.gov.govuk.chat

interface ChatFeature {

    suspend fun clear()

    suspend fun isEnabled(): Boolean

}
