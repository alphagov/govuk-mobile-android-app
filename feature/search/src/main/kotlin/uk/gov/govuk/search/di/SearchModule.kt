package uk.gov.govuk.search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.search.data.remote.AutocompleteApi
import uk.gov.govuk.search.data.remote.SearchApi
import uk.gov.govuk.search.domain.SearchConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SearchModule {

    @Provides
    @Singleton
    fun providesSearchApi(): SearchApi {
        return Retrofit.Builder()
            .baseUrl(SearchConfig.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApi::class.java)
    }


    @Provides
    @Singleton
    fun providesAutocompleteApi(): AutocompleteApi {
        return Retrofit.Builder()
            .baseUrl(SearchConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AutocompleteApi::class.java)
    }
}
