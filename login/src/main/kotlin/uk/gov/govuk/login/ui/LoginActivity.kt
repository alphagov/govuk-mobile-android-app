package uk.gov.govuk.login.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginUiState
import uk.gov.govuk.login.R
import uk.gov.govuk.login.data.remote.LoginApi
import uk.gov.govuk.login.di.AuthRequestModule
import uk.gov.govuk.login.di.LoginModule
import javax.inject.Inject

class LoginActivity: ComponentActivity() {
    private var authService: AuthorizationService? = null

    @Inject lateinit var loginApi: LoginApi

    private val loginResponseFlow = MutableStateFlow<String?>(null)
    private val accessTokenFlow = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authService = AuthorizationService(this@LoginActivity)
        loginApi = LoginModule().providesLoginApi()

        val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { data ->
                    val authResponse = AuthorizationResponse.fromIntent(data)
                    authResponse?.let { response ->
                        authService!!.performTokenRequest(
                            response.createTokenExchangeRequest()
                        ) { tokenResponse, _ ->
                            tokenResponse?.accessToken?.let { accessToken ->
                                accessTokenFlow.value = accessToken
                                println("Access token: $accessToken")

                                lifecycleScope.launch {
                                    try {
                                        val loginResponse = loginApi.get(accessToken)
                                        if (loginResponse.isSuccessful) {
                                            loginResponseFlow.value = loginResponse.body()
                                        } else {
                                            loginResponseFlow.value = "Login not successful"
                                        }
                                    } catch (e: Exception) {
                                        loginResponseFlow.value = "Exception: ${e.message}"
                                    }
                                }
                            } ?: run { loginResponseFlow.value = "No access token" }
                        }
                    } ?: run { loginResponseFlow.value = "No auth response" }
                } ?: run { loginResponseFlow.value = "No result data" }

            } else {
                loginResponseFlow.value = "Result code not ok: ${result.resultCode}"
            }
        }

        val authRequest = AuthRequestModule().providesAuthorizationRequest()
        val authIntent = authService!!.getAuthorizationRequestIntent(authRequest)

        setContent {
            GovUkTheme {
                LoginScreen(
                    loginResponseFlow,
                    accessTokenFlow,
                    onLogin = {
                        authLauncher.launch(authIntent)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService?.dispose()
    }
}


@Composable
private fun LoginScreen(
    loginResponseFlow: StateFlow<String?>,
    accessTokenFlow: StateFlow<String?>,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loginResponse by loginResponseFlow.collectAsState()
    val accessToken by accessTokenFlow.collectAsState()

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            FullScreenHeader(
                modifier = modifier.padding(bottom = GovUkTheme.spacing.large)
            )
        },
        bottomBar = {
            BottomNavBar(
                onLogin = onLogin,
                modifier = modifier
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.large)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SmallVerticalSpacer()
//            TODO: Add image when it becomes available...
//            Image(
//                painter = painterResource(id = R.drawable.xyz),
//                contentDescription = null,
//                modifier = Modifier
//            )
            MediumVerticalSpacer()
            LargeTitleBoldLabel(
                text = stringResource(R.string.login_sign_in_with_gov_uk),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.login_sign_sub_text),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
            if (loginResponse != null) {
                SmallVerticalSpacer()
                BodyRegularLabel(
                    text = loginResponse!!,
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                    textAlign = TextAlign.Center
                )
            }
            if (accessToken != null) {
                SmallVerticalSpacer()
                BodyRegularLabel(
                    text = accessToken!!,
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    onLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.login_continue_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = onLogin,
        )
    }
}

//@Composable
//private fun LoginScreen(
//    loginResponseFlow: StateFlow<String?>,
//    onLogin: () -> Unit
//) {
//    val loginResponse by loginResponseFlow.collectAsState()
//
//    Surface(
//        modifier =
//            Modifier
//                .fillMaxSize(),
//        color = GovUkTheme.colourScheme.surfaces.background
//    ) {
//        Column(
//            Modifier
//                .padding(horizontal = GovUkTheme.spacing.medium)
//                .padding(top = 50.dp)
//        ) {
//            PrimaryButton(
//                text = "Login!",
//                onClick = onLogin
//            )
//
//            Spacer(Modifier.height(GovUkTheme.spacing.extraLarge))
//
//            Text(loginResponse ?: "")
//        }
//    }
//}
