package uk.govuk.app.topics

import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.TopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsRepo @Inject constructor(
    private val topicsApi: TopicsApi
) {

    suspend fun getTopics(): List<TopicItem>? {
        return try {
            val response = topicsApi.getTopics()
            if (response.isSuccessful) {
                response.body()?.let {
                    it
                } ?: null // Todo - handle failure
            } else {
                // Todo - handle failure
                null
            }
        } catch (e: Exception) {
            // Todo - handle failure
            null
        }
    }
}