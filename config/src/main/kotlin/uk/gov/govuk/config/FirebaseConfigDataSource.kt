package uk.gov.govuk.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseConfigDataSource @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {

    suspend fun fetchAndActivate(): Boolean {
        return try {
            firebaseRemoteConfig.fetchAndActivate().await()
        } catch (_: Exception) {
            false
        }
    }
}