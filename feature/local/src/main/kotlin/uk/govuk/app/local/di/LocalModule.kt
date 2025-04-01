package uk.govuk.app.local.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.local.data.remote.LocalApi
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.ApiResponseAdapter
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalModule {
    @Provides
    @Singleton
    fun providesLocalApi(): LocalApi {
        val gson = GsonBuilder()
            .registerTypeAdapter(ApiResponse::class.java, ApiResponseAdapter(Gson()))
            .create()

        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:4567")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(LocalApi::class.java)
    }
}
