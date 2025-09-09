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
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.chat.ChatTestEndedViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatTestEndedRoute(
    launchBrowser: (url: String) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatTestEndedViewModel = hiltViewModel()
    val chatUrls = viewModel.chatUrls

    ChatTestEndedScreen(
        onPageView = { viewModel.onPageView() },
        onFeedback = { text ->
            viewModel.onLinkClick(text, chatUrls.feedback)
            launchBrowser(chatUrls.feedback)
        },
        onContinue = { text ->
            viewModel.onContinueClick(text)
            navigateToHome()
        },
        modifier = modifier
    )
}

@Composable
private fun ChatTestEndedScreen(
    onPageView: () -> Unit,
    onFeedback: (text: String) -> Unit,
    onContinue: (text: String) -> Unit,
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
                text = stringResource(id = R.string.test_ended_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.test_ended_para_1),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )

            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.test_ended_para_2),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            val feedbackText = stringResource(id = R.string.test_ended_feedback)
            SecondaryButton(
                text = feedbackText,
                onClick = {
                    onFeedback(feedbackText)
                },
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.small),
                externalLink = true
            )
        },
        footerContent = {
            val buttonText = stringResource(R.string.test_ended_button)

            FixedPrimaryButton(
                text = buttonText,
                onClick = { onContinue(buttonText) },
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
        ChatTestEndedScreen(
            onPageView = { },
            onFeedback = { },
            onContinue = { }
        )
    }
}

@Preview(
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModeChatOptInScreenPreview() {
    GovUkTheme {
        ChatTestEndedScreen(
            onPageView = { },
            onFeedback = { },
            onContinue = { }
        )
    }
}
