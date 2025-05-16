package uk.gov.govuk.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ErrorPage
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

    ErrorPage(
        headerText = stringResource(R.string.login_sign_in_error_header),
        subText = stringResource(R.string.login_sign_in_error_sub_text),
        buttonText = stringResource(R.string.login_back_and_try_again_button),
        onBack = onBackClick,
        modifier = modifier
    )
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
