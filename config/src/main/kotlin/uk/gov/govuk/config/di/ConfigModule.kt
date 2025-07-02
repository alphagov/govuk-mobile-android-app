package uk.gov.govuk.config.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.govuk.config.BuildConfig
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.DebugFlags
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.config.data.remote.ConfigApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ConfigModule {

    @Provides
    @Singleton
    fun providesConfigApi(): ConfigApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CONFIG_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ConfigApi::class.java)
    }

    @Provides
    @Singleton
    fun providesGson() = Gson()

    @Provides
    @Singleton
    fun providesFlagRepo(
        debugFlags: DebugFlags,
        configRepo: ConfigRepo
    ): FlagRepo {
        return FlagRepo(
            debugEnabled = BuildConfig.DEBUG,
            debugFlags = debugFlags,
            configRepo = configRepo
        )
    }
}
