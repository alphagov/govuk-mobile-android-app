package uk.gov.govuk.chat.ui

import android.content.res.Configuration
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import uk.gov.govuk.chat.ChatOptInViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatOptInRoute(
    launchBrowser: (url: String) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatOptInViewModel = hiltViewModel()
    val chatUrls = viewModel.chatUrls

    ChatOptInScreen(
        onPageView = { viewModel.onPageView() },
        onClickPrivacyNotice = { launchBrowser(chatUrls.privacyNotice) },
        onClickTermsAndConditions = { launchBrowser(chatUrls.termsAndConditions) },
        onClickOptIn = { text ->
            viewModel.onButtonClick(text)
            viewModel.onOptInClicked()
            navigateToHome()
        },
        onClickOptOut = { text ->
            viewModel.onButtonClick(text)
            viewModel.onOptOutClicked()
            navigateToHome()
        },
        modifier = modifier
    )
}

@Composable
private fun ChatOptInScreen(
    onPageView: () -> Unit,
    onClickPrivacyNotice: () -> Unit,
    onClickTermsAndConditions: () -> Unit,
    onClickOptIn: (text: String) -> Unit,
    onClickOptOut: (text: String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            val isLogoVisible =
                LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (isLogoVisible) {
                Image(
                    painter = painterResource(id = R.drawable.onboarding_page_one),
                    contentDescription = null,
                    modifier = Modifier.height(150.dp)
                )
                LargeVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.optin_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.optin_para_1),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )

            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.optin_para_2),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )

            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.optin_para_3),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            SecondaryButton(
                text = stringResource(id = R.string.optin_privacy_notice),
                onClick = {
                    onClickPrivacyNotice()
                },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.small),
                externalLink = true
            )

            SmallVerticalSpacer()

            SecondaryButton(
                text = stringResource(id = R.string.optin_terms_and_conditions),
                onClick = {
                    onClickTermsAndConditions()
                },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.small),
                externalLink = true
            )
        },
        footerContent = {
            val optInText = stringResource(R.string.optin_opt_in)
            val optOutText = stringResource(R.string.optin_opt_out)

            FixedDoubleButtonGroup(
                primaryText = optInText,
                onPrimary = { onClickOptIn(optInText) },
                secondaryText = optOutText,
                onSecondary = { onClickOptOut(optOutText) }
            )
        }
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModeChatOptInScreenPreview() {
    GovUkTheme {
        ChatOptInScreen(
            onPageView = { },
            onClickPrivacyNotice = { },
            onClickTermsAndConditions = { },
            onClickOptIn = { _ -> },
            onClickOptOut = { _ -> }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModeChatOptInScreenPreview() {
    GovUkTheme {
        ChatOptInScreen(
            onPageView = { },
            onClickPrivacyNotice = { },
            onClickTermsAndConditions = { },
            onClickOptIn = { _ -> },
            onClickOptOut = { _ -> }
        )
    }
}
