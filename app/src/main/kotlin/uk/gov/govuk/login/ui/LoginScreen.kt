package uk.gov.govuk.login.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginEvent
import uk.gov.govuk.login.LoginViewModel

@Composable
internal fun LoginRoute(
    onLoginCompleted: (LoginEvent) -> Unit,
    onError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()

    val authLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onAuthResponse(result.data)
        }
    }

    LoginScreen(
        onPageView = { viewModel.onPageView() },
        onContinueClick = { text ->
            viewModel.onContinue(text)
            authLauncher.launch(viewModel.authIntent)
        },
        modifier = modifier
    )

    val activity = LocalActivity.current as FragmentActivity

    LaunchedEffect(Unit) {
        viewModel.init(activity)
    }

    LaunchedEffect(Unit) {
        viewModel.loginCompleted.collect { loginEvent ->
            onLoginCompleted(loginEvent)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect {
            onError()
        }
    }
}

@Composable
private fun LoginScreen(
    onPageView: () -> Unit,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val shouldShowLogo =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (shouldShowLogo) {
                Image(
                    painter = painterResource(id = R.drawable.ic_welcome),
                    contentDescription = null,
                    modifier = Modifier.height(209.dp)
                )
                ExtraLargeVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.login_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            Title1RegularLabel(
                text = stringResource(id = R.string.login_description),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        },
        footerContent = {
            val buttonText = stringResource(R.string.login_continue_button)
            FixedPrimaryButton(
                text = buttonText,
                onClick = { onContinueClick(buttonText) }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    GovUkTheme {
        LoginScreen(
            onPageView = { },
            onContinueClick = { }
        )
    }
}