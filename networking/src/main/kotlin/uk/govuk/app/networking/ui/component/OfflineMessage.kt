package uk.govuk.app.networking.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.component.Error
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.R

@Composable
fun OfflineMessage(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester()
) {
    Error(
        title = stringResource(R.string.no_internet_title),
        description = stringResource(R.string.no_internet_description),
        buttonTitle = stringResource(R.string.try_again),
        onButtonClick = onButtonClick,
        modifier = modifier,
        focusRequester = focusRequester
    )
}

@Preview
@Composable
private fun OfflineMessagePreview() {
    GovUkTheme {
        OfflineMessage(onButtonClick = {})
    }
}
