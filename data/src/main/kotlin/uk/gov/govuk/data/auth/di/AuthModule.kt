package uk.gov.govuk.data.auth.di

import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.core.net.toUri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.SharedPrefsStore
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthModule {
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

    @Singleton
    @Provides
    fun provideAuthService(@ApplicationContext context: Context): AuthorizationService {
        return AuthorizationService(context)
    }

    @Singleton
    @Provides
    fun provideAuthIntent(authService: AuthorizationService): Intent {
        // Todo - extract into build variables!!!
        val authConfig = AuthorizationServiceConfiguration(
            "https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/authorize".toUri(),
            "https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/token".toUri()
        )

        val authRequestBuilder = AuthorizationRequest.Builder(
            authConfig,
            "121f51j1s4kmk9i98um0b5mphh",
            ResponseTypeValues.CODE,
            "govuk://govuk/login-auth-callback".toUri()
        )

        val authRequest = authRequestBuilder
            .setScopes("openid")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }
}