package uk.gov.govuk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: ComponentActivity() {

    @Inject lateinit var loginApi: LoginApi

    private val loginResponseFlow = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authService = AuthorizationService(this@LoginActivity)
        val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val authResponse = AuthorizationResponse.fromIntent(data)
                    authResponse?.let { response ->
                        authService.performTokenRequest(
                            response.createTokenExchangeRequest()
                        ) { tokenResponse, _ ->
                            tokenResponse?.accessToken?.let { accessToken ->
                                lifecycleScope.launch {
                                    try {
                                        val loginResponse = loginApi.get(accessToken)
                                        if (loginResponse.isSuccessful) {
                                            loginResponseFlow.value = loginResponse.body()
                                        } else {
                                            loginResponseFlow.value = "ERROR!!!"
                                        }
                                    } catch (e: Exception) {
                                        loginResponseFlow.value = "ERROR!!!"
                                    }
                                }
                            } ?: run { loginResponseFlow.value = "ERROR!!!" }
                        }
                    } ?: run { loginResponseFlow.value = "ERROR!!!" }
                } ?: run { loginResponseFlow.value = "ERROR!!!" }

            } else {
                loginResponseFlow.value = "ERROR!!!"
            }
        }

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

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)

        setContent {
            GovUkTheme {
                LoginScreen(loginResponseFlow) {
                    authLauncher.launch(authIntent)
                }
            }
        }
    }
}

@Composable
private fun LoginScreen(
    loginResponseFlow: StateFlow<String?>,
    onLogin: () -> Unit
) {
    val loginResponse by loginResponseFlow.collectAsState()

    Surface(
        modifier =
            Modifier
                .fillMaxSize(),
        color = GovUkTheme.colourScheme.surfaces.background
    ) {
        Column(
            Modifier
                .padding(horizontal = GovUkTheme.spacing.medium)
                .padding(top = 50.dp)
        ) {
            PrimaryButton(
                text = "Login!",
                onClick = onLogin
            )

            Spacer(Modifier.height(GovUkTheme.spacing.extraLarge))

            Text(loginResponse ?: "")
        }
    }
}