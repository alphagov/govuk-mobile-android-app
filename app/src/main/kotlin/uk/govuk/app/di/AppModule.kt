package uk.govuk.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.logging.MemorisedAnalyticsLogger
import uk.gov.logging.impl.analytics.FirebaseAnalyticsLogger
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class AppModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.preferencesDataStoreFile("app_preferences") }
        )
    }

    @InstallIn(SingletonComponent::class)
    @Module
    object AnalyticsSingletonModule {
        @Provides
        @Singleton
        fun providesAnalyticsLogger(
            analyticsLogger: FirebaseAnalyticsLogger
        ): AnalyticsLogger = MemorisedAnalyticsLogger(analyticsLogger)
    }

    @InstallIn(SingletonComponent::class)
    @Module
    class FirebaseSingletonModule {
        @Provides
        @Singleton
        fun providesFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
    }
}