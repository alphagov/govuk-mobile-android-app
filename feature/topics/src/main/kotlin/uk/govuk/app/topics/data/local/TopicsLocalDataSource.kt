package uk.govuk.app.topics.data.local

import android.util.Log
import io.realm.kotlin.ext.query
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopicsLocalDataSource @Inject constructor(
    private val realmProvider: TopicsRealmProvider
) {

    suspend fun getTopics() {
        val realm = realmProvider.open()

        val topic = LocalTopicItem().apply {
            ref = "ref"
            title = "title"
        }

        realm.write {
            copyToRealm(topic)
        }

        val topics = realm.query<LocalTopicItem>().find()
        for (localTopic in topics) {
            Log.d("Topics", "${localTopic.ref} ${localTopic.title}")
        }
    }

}