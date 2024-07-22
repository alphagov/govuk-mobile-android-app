package uk.govuk.app.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.logging.api.analytics.logging.AnalyticsLogger
import uk.gov.logging.api.analytics.logging.MemorisedAnalyticsLogger
import uk.gov.logging.impl.analytics.FirebaseAnalyticsLogger
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.analytics.AnalyticsClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalytics(analyticsClient: AnalyticsClient): Analytics = analyticsClient

    @Provides
    @Singleton
    fun provideAnalyticsLogger(
        analyticsLogger: FirebaseAnalyticsLogger
    ): AnalyticsLogger = MemorisedAnalyticsLogger(analyticsLogger)

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics = Firebase.analytics
}