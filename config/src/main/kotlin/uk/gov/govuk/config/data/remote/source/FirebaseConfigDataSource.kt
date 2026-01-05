package uk.gov.govuk.config.data.remote.source

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfigDataSource @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    suspend fun fetch(): Boolean {
        return try {
            firebaseRemoteConfig.fetch().await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun activate(): Boolean {
        return try {
            firebaseRemoteConfig.activate().await()
        } catch (_: Exception) {
            false
        }
    }
}