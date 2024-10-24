package uk.govuk.app.visited.data.store

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import uk.govuk.app.visited.data.model.VisitedItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VisitedRealmProvider @Inject constructor(
    private val encryptionHelper: VisitedEncryptionHelper
) {
    private companion object {
        private const val REALM_NAME = "visited"
    }

    private lateinit var realm: Realm

    suspend fun open(): Realm {
        if (!::realm.isInitialized) {
            val realmKey = encryptionHelper.getRealmKey()

            val config = RealmConfiguration.Builder(schema = setOf(VisitedItem::class))
                .name(REALM_NAME)
                .encryptionKey(realmKey)
                .build()
            realm = Realm.open(config)
        }

        return realm
    }
}
