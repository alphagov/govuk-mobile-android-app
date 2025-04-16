package uk.govuk.app.local.data.local

import io.realm.kotlin.ext.query
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
import uk.govuk.app.local.data.local.model.StoredLocalAuthorityParent
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalDataSource @Inject constructor(
    private val realmProvider: LocalRealmProvider
) {
    val localAuthority: Flow<StoredLocalAuthority?> = flow {
        emitAll(
            realmProvider.open()
                .query<StoredLocalAuthority>()
                .find()
                .asFlow()
                .map {
                    it.list.firstOrNull()
                }
        )
    }

    suspend fun insertOrReplace(
        localAuthority: RemoteLocalAuthority
    ) {
        realmProvider.open().write {
            query<StoredLocalAuthority>().find {
                delete(it)
            }

            copyToRealm(
                StoredLocalAuthority().apply {
                    this.name = localAuthority.name
                    this.url = localAuthority.homePageUrl
                    this.slug = localAuthority.slug
                    this.parent = localAuthority.parent?.let { parent ->
                        StoredLocalAuthorityParent().apply {
                            this.name = parent.name
                            this.url = parent.homePageUrl
                            this.slug = parent.slug
                        }
                    }
                }
            )
        }
    }
}
