package uk.gov.govuk.data.auth.di

import android.content.Context
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
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.TokenRequest
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
    fun provideAuthServiceConfig(): AuthorizationServiceConfiguration {
        // Todo - extract into build variables!!!
        return AuthorizationServiceConfiguration(
            "https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/authorize".toUri(),
            "https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/token".toUri()
        )
    }

    @Singleton
    @Provides
    fun provideAuthRequest(authConfig: AuthorizationServiceConfiguration): AuthorizationRequest {
        // Todo - extract into build variables!!!
        val authRequestBuilder = AuthorizationRequest.Builder(
            authConfig,
            "121f51j1s4kmk9i98um0b5mphh",
            ResponseTypeValues.CODE,
            "govuk://govuk/login-auth-callback".toUri()
        )

        return authRequestBuilder
            .setScopes("openid")
            .build()
    }

    @Singleton
    @Provides
    fun provideTokenRequestBuilder(authConfig: AuthorizationServiceConfiguration): TokenRequest.Builder {
        val tokenRequestBuilder = TokenRequest.Builder(
            authConfig,
            "121f51j1s4kmk9i98um0b5mphh"
        )

        return tokenRequestBuilder
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
    }
}