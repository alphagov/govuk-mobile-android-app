package uk.gov.govuk.login.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import uk.gov.govuk.login.BuildConfig
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.govuk.login.data.remote.LoginApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LoginModule {
    @Provides
    @Singleton
    fun providesLoginApi(): LoginApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.LOGIN_SERVICE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }
}
