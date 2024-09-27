package uk.govuk.app.topics.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.topics.data.remote.TopicsApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class TopicsModule {

    @Provides
    @Singleton
    fun providesTopicsApi(): TopicsApi {
        return Retrofit.Builder()
            .baseUrl("https://app.integration.publishing.service.gov.uk/static/topics/") // Todo - extract base url into build config
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TopicsApi::class.java)
    }

}