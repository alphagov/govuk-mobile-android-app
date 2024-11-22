package uk.govuk.app.topics.data.local

import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.topics.data.local.model.LocalTopicItem
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

    suspend fun selectAll(refs: List<String>) {
        insertOrUpdate(
            refs.map { ref -> Pair(ref, true) }
        )
    }

    suspend fun select(ref: String) {
        insertOrUpdate(listOf(Pair(ref, true)))
    }

    suspend fun deselect(ref: String) {
        insertOrUpdate(listOf(Pair(ref, false)))
    }

    private suspend fun insertOrUpdate(topics: List<Pair<String, Boolean>>) {
        realmProvider.open().write {
            for ((ref, isSelected) in topics) {
                val localTopic = query<LocalTopicItem>("ref = $0", ref).first().find()

                localTopic?.apply {
                    this.isSelected = isSelected
                } ?: copyToRealm(
                    LocalTopicItem().apply {
                        this.ref = ref
                        this.isSelected = isSelected
                    }
                )
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