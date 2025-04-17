package uk.gov.govuk.login.di

import androidx.core.net.toUri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import uk.gov.govuk.login.BuildConfig
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AuthRequestModule {
    @Provides
    @Singleton
    fun providesAuthorizationRequest(): AuthorizationRequest {
        return AuthorizationRequest.Builder(
            AuthorizationServiceConfiguration(
                BuildConfig.AUTHORIZE_URL.toUri(),
                BuildConfig.TOKEN_URL.toUri()
            ),
            BuildConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            BuildConfig.REDIRECT_URI.toUri()
        ).setScopes(BuildConfig.SCOPE).build()
    }
}
