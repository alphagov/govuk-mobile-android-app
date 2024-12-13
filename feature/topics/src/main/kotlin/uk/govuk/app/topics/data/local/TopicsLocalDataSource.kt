package uk.govuk.app.topics.data.local

import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsLocalDataSource @Inject constructor(
    private val realmProvider: TopicsRealmProvider,
    private val topicsDataStore: TopicsDataStore
) {

    val topics: Flow<List<LocalTopicItem>> = flow {
        emitAll(realmProvider.open().query<LocalTopicItem>().asFlow().map { it.list })
    }

    suspend fun sync(remoteTopics: List<RemoteTopicItem>) {
        val localTopics = realmProvider.open().query<LocalTopicItem>().find().toList()
        val topicsToDelete = localTopics.filter { !remoteTopics.map { it.ref }.contains(it.ref) }

        val isSelected = !isTopicsCustomised()

        realmProvider.open().writeBlocking {
            for (topic in topicsToDelete) {
                delete(topic)
            }
        }

        for (topic in remoteTopics) {
            insertOrUpdate(topic, isSelected)
        }
    }

    private suspend fun insertOrUpdate(topic: RemoteTopicItem, isSelected: Boolean) {
        realmProvider.open().writeBlocking {
            val localTopic = query<LocalTopicItem>("ref = $0", topic.ref).first().find()

            localTopic?.apply {
                this.title = topic.title
                this.description = topic.description
            } ?: copyToRealm(
                LocalTopicItem().apply {
                    this.ref = topic.ref
                    this.title = topic.title
                    this.description = topic.description
                    this.isSelected = isSelected
                }
            )
        }
    }

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        realmProvider.open().write {
            query<LocalTopicItem>("ref = $0", ref).first().find()?.apply {
                this.isSelected = isSelected
            }
        }
    }

    internal suspend fun isTopicsCustomised(): Boolean {
        return topicsDataStore.isTopicsCustomised()
    }

    internal suspend fun topicsCustomised() {
        topicsDataStore.topicsCustomised()
    }
}