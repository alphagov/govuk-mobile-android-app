package uk.gov.govuk.login.di

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import uk.gov.govuk.login.BuildConfig
import uk.gov.govuk.login.LoginFeature
import uk.gov.govuk.login.LoginFeatureProvider
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

    @Provides
    @Singleton
    fun providesLoginFeature(biometricManager: BiometricManager): LoginFeature {
        return LoginFeatureProvider(biometricManager)
    }

    @Singleton
    @Provides
    fun provideSecureStore(@ApplicationContext context: Context): SecureStore {
        val secureStore = SharedPrefsStore()
        secureStore.init(
            context,
            SecureStorageConfiguration("gov-uk-secure-store", AccessControlLevel.PASSCODE_AND_CURRENT_BIOMETRICS)
        )
        return secureStore
    }

    @Singleton
    @Provides
    fun provideBiometricManager(@ApplicationContext context: Context): BiometricManager {
        return BiometricManager.from(context)
    }
}
