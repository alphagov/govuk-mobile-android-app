package uk.govuk.app.topics.data.local

import io.realm.kotlin.ext.query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.forEach

@Singleton
internal class TopicsLocalDataSource @Inject constructor(
    private val realmProvider: TopicsRealmProvider,
    private val topicsDataStore: TopicsDataStore
) {

    val topics: Flow<List<LocalTopicItem>> = flow {
        emitAll(realmProvider.open().query<LocalTopicItem>().asFlow().map { it.list })
    }

    suspend fun sync(remoteTopics: List<RemoteTopicItem>) {
        withContext(Dispatchers.IO) {
            val localTopics = realmProvider.open().query<LocalTopicItem>().find().toList()
            val topicsToDelete =
                localTopics.filter { !remoteTopics.map { it.ref }.contains(it.ref) }

            val isSelectedOnInsert = !isTopicsCustomised()

            realmProvider.open().write {
                for (topic in topicsToDelete) {
                    val liveTopic = findLatest(topic)
                    if (liveTopic != null) {
                        delete(liveTopic)
                    }
                }

                remoteTopics.forEach { topic ->
                    val localTopic = query<LocalTopicItem>("ref = $0", topic.ref).first().find()

                    localTopic?.apply {
                        this.title = topic.title
                        this.description = topic.description
                    } ?: copyToRealm(
                        LocalTopicItem().apply {
                            this.ref = topic.ref
                            this.title = topic.title
                            this.description = topic.description
                            this.isSelected = isSelectedOnInsert
                        }
                    )
                }
            }
        }
    }

    suspend fun toggleSelection(ref: String, isSelected: Boolean) {
        realmProvider.open().write {
            query<LocalTopicItem>("ref = $0", ref).first().find()?.apply {
                this.isSelected = isSelected
            }
        }
    }

    suspend fun deselectAll(refs: List<String>) {
        realmProvider.open().write {
            val topics = query<LocalTopicItem>("ref IN $0", refs).find()
            topics.forEach { topic ->
                findLatest(topic)?.isSelected = false
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