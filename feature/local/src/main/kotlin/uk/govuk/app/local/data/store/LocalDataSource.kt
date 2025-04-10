package uk.govuk.app.local.data.store

import io.realm.kotlin.ext.query
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.govuk.app.local.data.model.StoredLocalAuthority
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class LocalDataSource @Inject constructor(
    private val realmProvider: LocalRealmProvider
) {
    val storedLocalAuthority: Flow<StoredLocalAuthority> = flow {
        emit(
            realmProvider.open().query<StoredLocalAuthority>().first().find() ?: StoredLocalAuthority()
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
