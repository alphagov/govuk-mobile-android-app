package uk.govuk.app.search.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import uk.govuk.app.data.local.RealmEncryptionHelper
import uk.govuk.app.search.data.local.model.LocalSearchItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SearchRealmProvider @Inject constructor(
    private val encryptionHelper: RealmEncryptionHelper
) {

    private companion object {
        private const val REALM_NAME = "search"
    }

    private lateinit var realm: Realm

    suspend fun open(): Realm {
        if (!::realm.isInitialized) {
            val realmKey = encryptionHelper.getRealmKey()

            val config = RealmConfiguration.Builder(schema = setOf(LocalSearchItem::class))
                .schemaVersion(1)
                .name(REALM_NAME)
                .encryptionKey(realmKey)
                .build()
            realm = Realm.open(config)
        }

        return realm
    }
}