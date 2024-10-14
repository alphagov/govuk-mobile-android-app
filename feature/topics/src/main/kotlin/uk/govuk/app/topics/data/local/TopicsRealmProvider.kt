package uk.govuk.app.topics.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import uk.govuk.app.topics.data.local.model.LocalTopicItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TopicsRealmProvider @Inject constructor(
    private val encryptionHelper: TopicsEncryptionHelper
) {

    private companion object {
        private const val REALM_NAME = "topics"
    }

    private lateinit var realm: Realm

    suspend fun open(): Realm {
        if (!::realm.isInitialized) {
            val realmKey = encryptionHelper.getRealmKey()

            val config = RealmConfiguration.Builder(schema = setOf(LocalTopicItem::class))
                .name(REALM_NAME)
                .encryptionKey(realmKey)
                .build()
            realm = Realm.open(config)
        }

        return realm
    }
}