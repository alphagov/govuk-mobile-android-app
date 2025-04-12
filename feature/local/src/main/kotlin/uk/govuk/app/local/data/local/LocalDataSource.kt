package uk.govuk.app.local.data.local

import io.realm.kotlin.ext.query
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import uk.govuk.app.local.data.local.model.StoredLocalAuthority
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
                .map { it.list.firstOrNull() }
        )
    }

    suspend fun insertOrReplace(
        name: String,
        url: String,
        slug: String,
        parentName: String? = "",
        parentUrl: String? = "",
        parentSlug: String? = ""
    ) {
        realmProvider.open().write {
            query<StoredLocalAuthority>().find {
                delete(it)
            }

            copyToRealm(
                StoredLocalAuthority().apply {
                    this.name = name
                    this.url = url
                    this.slug = slug
                    this.parentName = parentName
                    this.parentUrl = parentUrl
                    this.parentSlug = parentSlug
                }
            )
        }
    }
}
