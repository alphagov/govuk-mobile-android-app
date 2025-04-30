package uk.gov.govuk.login.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginViewModel
import uk.gov.govuk.login.R

@Composable
internal fun LoginRoute(
    isPostSignOut: Boolean,
    onLogin: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onAuthResponse(result.data)
        }
    }

    LoginScreen(
        isPostSignOut = isPostSignOut,
        onPageView = { viewModel.onPageView() },
        onContinueClick = { text ->
            viewModel.onContinue(text)
            authLauncher.launch(viewModel.authIntent)
        },
        modifier = modifier
    )

    LaunchedEffect(uiState) {
        uiState?.let { loginState ->
            onLogin(loginState.isAuthenticationEnabled)
        }
    }
}

@Composable
private fun LoginScreen(
    isPostSignOut: Boolean,
    onPageView: () -> Unit,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.weight(1F))

            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .padding(vertical = GovUkTheme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val title = if (isPostSignOut) {
                    "You've signed out"
                } else {
                    stringResource(R.string.login_sign_in_with_gov_uk)
                }
                LargeTitleBoldLabel(
                    text = title,
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                val subtitle = if (isPostSignOut) {
                    "You need to sign in again to use the app"
                } else {
                    stringResource(R.string.login_sign_sub_text)
                }

                BodyRegularLabel(
                    text = subtitle,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        val buttonText = if (isPostSignOut) {
            "Sign in with GOV.UK One Login"
        } else {
            stringResource(R.string.login_continue_button)
        }
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onContinueClick(buttonText) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    GovUkTheme {
        LoginScreen(
            isPostSignOut = false,
            onPageView = { },
            onContinueClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPostSignOutPreview() {
    GovUkTheme {
        LoginScreen(
            isPostSignOut = true,
            onPageView = { },
            onContinueClick = { }
        )
    }
}
