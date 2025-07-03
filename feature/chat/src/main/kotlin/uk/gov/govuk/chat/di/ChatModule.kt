package uk.gov.govuk.chat.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.data.remote.ChatApi
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class ChatModule {
    @Provides
    @Singleton
    fun providesChatApi(): ChatApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CHAT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApi::class.java)
    }
}
