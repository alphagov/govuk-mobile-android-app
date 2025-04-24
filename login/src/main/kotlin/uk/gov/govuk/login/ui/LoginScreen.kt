package uk.gov.govuk.login.ui

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
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()
//    val uiState by viewModel.uiState.collectAsState()

    val authLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    LoginScreen(
//        onPageView = { viewModel.onPageView() },
        onPageView = { },
//        onContinueClick = { text ->
//            viewModel.onContinueClick(text)
//        },
        onContinueClick = {
            authLauncher.launch(viewModel.authIntent)
        },
        modifier = modifier
    )
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
                LargeTitleBoldLabel(
                    text = stringResource(R.string.login_sign_in_with_gov_uk),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_sign_sub_text),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        val buttonText = stringResource(R.string.login_continue_button)
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
            onPageView = { },
            onContinueClick = { }
        )
    }
}