package uk.gov.govuk.topics.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.topics.BuildConfig
import uk.gov.govuk.topics.DefaultTopicsFeature
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.topics.data.TopicsRepo
import uk.gov.govuk.topics.data.remote.TopicsApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class TopicsModule {

    @Provides
    @Singleton
    fun providesTopicFeature(topicsRepo: TopicsRepo): TopicsFeature {
        return DefaultTopicsFeature(topicsRepo)
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