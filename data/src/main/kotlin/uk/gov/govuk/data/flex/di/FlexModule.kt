package uk.gov.govuk.data.flex.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.data.BuildConfig
import uk.gov.govuk.data.flex.remote.FlexApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FlexModule {
    @Provides
    @Singleton
    fun providesFlexApi(): FlexApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.FLEX_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FlexApi::class.java)
    }
}
