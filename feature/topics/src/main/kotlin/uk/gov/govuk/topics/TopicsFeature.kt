package uk.gov.govuk.topics

interface TopicsFeature  {

    suspend fun init(): Boolean

    suspend fun clear()

}