package uk.govuk.app.search.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.search.domain.SearchConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SearchModule {

    @Provides
    @Singleton
    fun providesSearchApi(): SearchApi {
        return Retrofit.Builder()
            .baseUrl(SearchConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
    }
}
