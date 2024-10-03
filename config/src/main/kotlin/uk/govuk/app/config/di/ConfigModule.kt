package uk.govuk.app.config.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.config.data.remote.ConfigApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ConfigModule {

    @Provides
    @Singleton
    fun providesConfigApi(): ConfigApi {
        return Retrofit.Builder()
            .baseUrl("https://app.integration.publishing.service.gov.uk/config/") // Todo - extract base url into build config
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConfigApi::class.java)
    }

}