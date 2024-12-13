package uk.govuk.app.topics.data

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
                    localDataSource.sync(topics.map { it.ref })
                    success = true
                }
            }
        } catch (_: Exception) { }

        return success
    }

    private val remoteTopics = flow {
        try {
            val response = topicsApi.getTopics()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(it)
                } // Todo - handle failure
            } else {
                // Todo - handle failure
            }
        } catch (e: Exception) {
            // Todo - handle failure
        }
    }

    val topics = remoteTopics.combine(localDataSource.topics) { remoteTopics, localTopics ->
        return@combine remoteTopics.map { remoteTopic ->
            TopicItem(
                ref = remoteTopic.ref,
                title = remoteTopic.title,
                description = remoteTopic.description,
                isSelected = localTopics.isEmpty() || localTopics.any { localTopic ->
                    remoteTopic.ref == localTopic.ref && localTopic.isSelected
                }
            )
        }
    }

    suspend fun selectInitialTopics() {
        val isLocalEmpty = localDataSource.topics.first().isEmpty()
        if (isLocalEmpty) {
            localDataSource.selectAll(remoteTopics.first().map { it.ref })
        }
    }

    suspend fun selectTopic(ref: String) {
        localDataSource.select(ref)
    }

    suspend fun deselectTopic(ref: String) {
        localDataSource.deselect(ref)
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