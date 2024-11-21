package uk.govuk.app.analytics

import android.os.Bundle
import android.os.Parcelable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsClient @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) {

    fun enable() {
        firebaseAnalytics.setAnalyticsCollectionEnabled(true)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
    }

    fun disable() {
        firebaseAnalytics.setAnalyticsCollectionEnabled(false)
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(false)
    }

    fun logEvent(name: String, parameters: Map<String, Any>) {
        firebaseAnalytics.logEvent(name, mapToBundle(parameters))
    }

    fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    private fun mapToBundle(map: Map<String, Any>): Bundle {
        val bundle = Bundle()
        for ((key, value) in map) {
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Long -> bundle.putLong(key, value)
                is Bundle -> bundle.putBundle(key, value)
                is ArrayList<*> -> {
                    @Suppress("UNCHECKED_CAST") // Ensure proper type checks elsewhere
                    when {
                        value.isNotEmpty() && value[0] is String -> bundle.putStringArrayList(key, value as ArrayList<String>)
                        value.isNotEmpty() && value[0] is Int -> bundle.putIntegerArrayList(key, value as ArrayList<Int>)
                    }
                }
                is Serializable -> bundle.putSerializable(key, value)
                is Parcelable -> bundle.putParcelable(key, value)
                else -> throw IllegalArgumentException("Unsupported type for key: $key, value: $value")
            }
        }
        return bundle
    }
}