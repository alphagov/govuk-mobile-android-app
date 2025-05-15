package uk.gov.govuk.login.ui

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
import uk.gov.govuk.login.ErrorViewModel
import uk.gov.govuk.login.R

@Composable
internal fun ErrorRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ErrorViewModel = hiltViewModel()

    ErrorScreen(
        onPageView = { viewModel.onPageView() },
        onBackClick = { text ->
            viewModel.onBack(text)
            onBack() },
        modifier = modifier
    )
}

@Composable
private fun ErrorScreen(
    onPageView: () -> Unit,
    onBackClick: (String) -> Unit,
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.height(IntrinsicSize.Min)
                        .padding(all = GovUkTheme.spacing.medium)
                )

                LargeHorizontalSpacer()

                LargeTitleBoldLabel(
                    text = stringResource(R.string.login_sign_in_error_header),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_sign_in_error_sub_text),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        val buttonText = stringResource(R.string.login_back_and_try_again_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onBackClick(buttonText) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    GovUkTheme {
        ErrorScreen(
            onPageView = { },
            onBackClick = { }
        )
    }
}
