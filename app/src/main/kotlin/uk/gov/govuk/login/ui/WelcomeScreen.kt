package uk.gov.govuk.login.ui

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.WelcomeViewModel
import uk.gov.govuk.login.navigation.navigateToErrorScreen

@Composable
internal fun WelcomeRoute(
    navController: NavController,
    onLoginCompleted: (Boolean) -> Unit,
    isComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: WelcomeViewModel = hiltViewModel()

    val authLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onAuthResponse(result.data)
        }
    }

    WelcomeScreen(
        onPageView = { viewModel.onPageView() },
        onContinueClick = { text ->
            viewModel.onContinue(text)
            authLauncher.launch(viewModel.authIntent)
            isComplete()
        },
        modifier = modifier
    )

    val activity = LocalActivity.current as FragmentActivity

    LaunchedEffect(Unit) {
        viewModel.init(activity)
    }

    LaunchedEffect(Unit) {
        viewModel.loginCompleted.collect { isDifferentUser ->
            onLoginCompleted(isDifferentUser)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.errorEvent.collect {
            navController.navigateToErrorScreen()
        }
    }
}

@Composable
private fun WelcomeScreen(
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
                val shouldShowLogo =
                    LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                if (shouldShowLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.welcome_image),
                        contentDescription = null,
                        modifier = Modifier.height(209.dp)
                    )
                    ExtraLargeVerticalSpacer()
                }

                LargeTitleBoldLabel(
                    text = stringResource(R.string.welcomeTitle),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                Title1RegularLabel(
                    text = stringResource(R.string.welcomeBody),
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
private fun WelcomeScreenPreview() {
    GovUkTheme {
        WelcomeScreen(
            onPageView = { },
            onContinueClick = { }
        )
    }
}
