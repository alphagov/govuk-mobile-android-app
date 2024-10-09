package uk.govuk.app.topics.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import uk.govuk.app.topics.data.local.TopicsLocalDataSource
import uk.govuk.app.topics.data.remote.TopicsApi
import uk.govuk.app.topics.domain.model.TopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsRepo @Inject constructor(
    topicsApi: TopicsApi,
    private val localDataSource: TopicsLocalDataSource,
) {
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

    suspend fun getTopics(): Flow<List<TopicItem>> {
        return remoteTopics.combine(localDataSource.getTopics()) { remoteTopics, localTopics ->
            return@combine remoteTopics.map { remoteTopic ->
                TopicItem(
                    ref = remoteTopic.ref,
                    title = remoteTopic.title,
                    isSelected = localTopics.isEmpty() || localTopics.any { localTopic ->
                                remoteTopic.ref == localTopic.ref && localTopic.isSelected
                            }
                )
            }
        }
    }

    suspend fun selectInitialTopics() {
        val isLocalEmpty = localDataSource.getTopics().first().isEmpty()
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
}