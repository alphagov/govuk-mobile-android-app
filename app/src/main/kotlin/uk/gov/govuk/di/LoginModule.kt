package uk.gov.govuk.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.govuk.LoginApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LoginModule {

    @Provides
    @Singleton
    fun providesLoginApi(): LoginApi {
        return Retrofit.Builder()
            .baseUrl("https://aulmirij8h.execute-api.eu-west-2.amazonaws.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(LoginApi::class.java)
    }
}
