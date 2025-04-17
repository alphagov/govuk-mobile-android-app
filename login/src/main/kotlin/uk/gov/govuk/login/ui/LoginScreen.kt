package uk.gov.govuk.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.LoginUiState
import uk.gov.govuk.login.LoginViewModel
import uk.gov.govuk.login.R

@Composable
internal fun LoginRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onContinueClick = { text ->
            viewModel.onContinueClick(text)
        },
        modifier = modifier
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState?,
    onPageView: () -> Unit,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

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
                onContinueClick = onContinueClick,
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
            if (uiState != null) {
                SmallVerticalSpacer()
                BodyRegularLabel(
                    text = uiState.loginResponse,
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
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.login_continue_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onContinueClick(buttonText) },
        )
    }
}
