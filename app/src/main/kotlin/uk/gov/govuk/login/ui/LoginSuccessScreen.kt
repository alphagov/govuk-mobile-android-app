package uk.gov.govuk.login.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginSuccessViewModel

@Composable
internal fun LoginSuccessRoute(
    onLoginSuccessCompleted: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LoginSuccessViewModel = hiltViewModel()

    LoginSuccessScreen(
        onContinueClick = {
            viewModel.onContinue()
        },
        modifier = modifier
    )

    LaunchedEffect(Unit) {
        viewModel.loginSuccessCompleted.collect { loginSuccessEvent ->
            onLoginSuccessCompleted(loginSuccessEvent.isBiometricsEnabled)
        }
    }
}

@Composable
private fun LoginSuccessScreen(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val shouldShowLogo =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (shouldShowLogo) {
                Image(
                    painter = painterResource(id = R.drawable.ic_login_success),
                    contentDescription = null,
                    modifier = Modifier.height(209.dp)
                )
                LargeVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(R.string.login_success_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )
        },
        footerContent = {
            FixedPrimaryButton(
                text = stringResource(R.string.login_continue_button),
                onClick = { onContinueClick() }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginSuccessPreview() {
    GovUkTheme {
        LoginSuccessScreen(
            onContinueClick = { }
        )
    }
}