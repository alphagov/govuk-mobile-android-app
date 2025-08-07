package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.navigation.navigateToChat
import uk.gov.govuk.chat.navigation.navigateToOnboardingPageOne
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPageTwoRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()

    OnboardingPageTwoScreen(
        onClick = {
            viewModel.setChatIntroSeen()
            navController.navigateToChat()
        },
        onCancel = {
            navController.popBackStack()
        },
        onBack = {
            navController.navigateToOnboardingPageOne()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingPageTwoScreen(
    onClick: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    OnboardingPage(
        title = stringResource(id = R.string.onboarding_page_two_header),
        image = painterResource(id = R.drawable.onboarding_page_two),
        modifier = modifier,
        headerContent = {
            FullScreenHeader(
                onBack = onBack,
                onAction = onCancel,
                actionText = stringResource(id = R.string.onboarding_page_cancel_text)
            )
        },
        screenContent = {
            Column(
                modifier = Modifier.padding(all = GovUkTheme.spacing.medium)
                    .border(
                        1.dp,
                        GovUkTheme.colourScheme.strokes.chatIntroCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .background(GovUkTheme.colourScheme.surfaces.chatIntroCardBackground)
            ) {
                Row(
                    modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_info_24),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.icon,
                        modifier = Modifier.height(IntrinsicSize.Min)
                            .padding(end = GovUkTheme.spacing.medium)
                    )

                    BodyRegularLabel(
                        text = stringResource(id = R.string.onboarding_page_two_text_one),
                        textAlign = TextAlign.Start
                    )
                }

                Row(
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                ) {
                    HorizontalDivider(
                        modifier = Modifier,
                        thickness = 1.dp,
                        color = GovUkTheme.colourScheme.strokes.chatIntroCardBorder
                    )
                }

                Row(
                    modifier = Modifier.padding(all = GovUkTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_list_alt_check_24),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.icon,
                        modifier = Modifier.height(IntrinsicSize.Min)
                            .padding(end = GovUkTheme.spacing.medium)
                    )

                    BodyRegularLabel(
                        text = stringResource(id = R.string.onboarding_page_two_text_two),
                        textAlign = TextAlign.Start
                    )
                }
            }
        },
        buttonContent = {
            PrimaryButton(
                text = stringResource(id = R.string.onboarding_page_two_button),
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
        OnboardingPageTwoScreen(
            onClick = {},
            onCancel = {},
            onBack = {}
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
        OnboardingPageTwoScreen(
            onClick = {},
            onCancel = {},
            onBack = {}
        )
    }
}
