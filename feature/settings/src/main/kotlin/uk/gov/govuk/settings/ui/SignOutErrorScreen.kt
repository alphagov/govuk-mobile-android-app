package uk.gov.govuk.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.settings.R
import uk.gov.govuk.settings.SignOutViewModel

@Composable
internal fun SignOutErrorRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SignOutViewModel = hiltViewModel()

    SignOutErrorScreen(
        onPageView = { viewModel.onErrorPageView() },
        onBackClick = { text ->
            viewModel.onBack(text)
            onBack() },
        modifier = modifier
    )
}

@Composable
private fun SignOutErrorScreen(
    onPageView: () -> Unit,
    onBackClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    // TODO: Move the actual screen as a component into the Design module...

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
                Icon(
                    painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_error),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.height(IntrinsicSize.Min)
                        .padding(all = GovUkTheme.spacing.medium)
                )

                LargeHorizontalSpacer()

                LargeTitleBoldLabel(
                    text = stringResource(R.string.sign_out_error_header),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.sign_out_error_sub_text),
                    textAlign = TextAlign.Center
                )

                // TODO: this is optional in the component
                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.sign_out_error_additional_text),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        val buttonText = stringResource(R.string.sign_out_error_go_back_to_settings_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onBackClick(buttonText) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignOutErrorScreenPreview() {
    GovUkTheme {
        SignOutErrorScreen(
            onPageView = { },
            onBackClick = { }
        )
    }
}
