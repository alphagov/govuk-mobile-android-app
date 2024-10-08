package uk.govuk.app.topics.data

import uk.govuk.app.topics.data.local.TopicsLocalDataSource
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.TopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsRepo @Inject constructor(
    private val topicsApi: TopicsApi,
    private val localDataSource: TopicsLocalDataSource,
) {

    suspend fun getTopics(): List<TopicItem>? {
        localDataSource.getTopics()

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