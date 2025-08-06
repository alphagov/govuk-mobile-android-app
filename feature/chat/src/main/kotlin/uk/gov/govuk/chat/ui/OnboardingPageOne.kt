package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.navigation.navigateToOnboardingPageTwo
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPageOneRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    OnboardingPageOneScreen(
        onClick = {
            navController.navigateToOnboardingPageTwo()
        },
        onCancel = {
            navController.popBackStack()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingPageOneScreen(
    onClick: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page_one_header),
        image = painterResource(id = R.drawable.onboarding_page_one),
        modifier = modifier,
        headerContent = {
            FullScreenHeader(
                onAction = onCancel,
                actionText = stringResource(id = R.string.onboarding_page_cancel_text)
            )
        },
        screenContent = {
            MediumVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(id = R.string.onboarding_page_one_text),
                textAlign = TextAlign.Center
            )
        },
        buttonContent = {
            PrimaryButton(
                text = stringResource(id = R.string.onboarding_page_one_button),
                onClick = onClick,
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.medium)
                    .background(GovUkTheme.colourScheme.surfaces.fixedContainer),
                enabled = true,
                externalLink = false
            )
        }
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModePreview() {
    GovUkTheme {
        OnboardingPageOneScreen(
            onClick = {},
            onCancel = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModePreview() {
    GovUkTheme {
        OnboardingPageOneScreen(
            onClick = {},
            onCancel = {}
        )
    }
}
