package uk.govuk.app.topics.data

import kotlinx.coroutines.flow.map
import uk.govuk.app.networking.domain.ApiException
import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.networking.domain.ServiceNotRespondingException
import uk.govuk.app.topics.data.local.TopicsLocalDataSource
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.data.remote.model.RemoteTopic
import uk.govuk.app.topics.domain.model.TopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsRepo @Inject constructor(
    private val topicsApi: TopicsApi,
    private val localDataSource: TopicsLocalDataSource,
) {

    suspend fun sync(): Boolean {
        var success = false
        try {
            val response = topicsApi.getTopics()
            if (response.isSuccessful) {
                response.body()?.let { topics ->
                    localDataSource.sync(topics)
                    success = true
                }
            }
        } catch (_: Exception) { }

        return success
    }

    val topics = localDataSource.topics.map { localTopics ->
        localTopics.map { localTopic ->
            TopicItem(
                ref = localTopic.ref,
                title = localTopic.title,
                description = localTopic.description,
                isSelected = localTopic.isSelected
            )
        }
    }

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        localDataSource.topicsCustomised()
        localDataSource.toggleSelection(ref, isSelected)
    }

    suspend fun getTopic(ref: String): Result<RemoteTopic> {
        return try {
            val response = topicsApi.getTopic(ref)
            if (response.isSuccessful) {
                response.body()?.let {
                    return Result.success(it)
                }
            }
            Result.failure(ApiException())
        } catch (e: java.net.UnknownHostException) {
            Result.failure(DeviceOfflineException())
        } catch (e: retrofit2.HttpException) {
            Result.failure(ServiceNotRespondingException())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return localDataSource.isTopicsCustomised()
    }

    internal suspend fun topicsCustomised() {
        localDataSource.topicsCustomised()
    }
}