package uk.govuk.app.search.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchResultsRetrofitInstance {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SearchConfig.GOV_UK_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val searchResultsService: SearchResultsService by lazy {
        retrofit.create(SearchResultsService::class.java)
    }
}
