package uk.govuk.app.topics.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.topics.BuildConfig
import uk.govuk.app.topics.TopicsFeature
import uk.govuk.app.topics.TopicsFeatureImpl
import uk.govuk.app.topics.data.remote.TopicsApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class TopicsModule {

    @Provides
    @Singleton
    fun providesTopicFeature(topicsFeature: TopicsFeatureImpl): TopicsFeature {
        return topicsFeature
    }

    @Provides
    @Singleton
    fun providesTopicsApi(): TopicsApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.TOPICS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TopicsApi::class.java)
    }

}