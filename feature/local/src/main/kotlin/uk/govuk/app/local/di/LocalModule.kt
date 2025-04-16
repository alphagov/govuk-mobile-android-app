package uk.govuk.app.local.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.govuk.app.local.BuildConfig
import uk.govuk.app.local.LocalFeature
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.LocalApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class LocalModule {

    @Provides
    @Singleton
    fun providesLocalFeature(localRepo: LocalRepo): LocalFeature {
        return LocalFeature { return@LocalFeature localRepo.localAuthority.map { it != null } }
    }

    @Provides
    @Singleton
    fun providesLocalApi(): LocalApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.LOCAL_SERVICES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocalApi::class.java)
    }
}
