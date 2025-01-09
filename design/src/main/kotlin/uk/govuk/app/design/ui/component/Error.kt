package uk.govuk.app.design.ui.component

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun Error(
    title: String,
    description: String,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    externalLink: Boolean = false,
    focusRequester: FocusRequester = FocusRequester()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = GovUkTheme.spacing.large),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BodyBoldLabel(
            text = title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
        )

        SmallVerticalSpacer()

        BodyRegularLabel(
            text = description,
            textAlign = TextAlign.Center
        )

        SmallVerticalSpacer()

        SecondaryButton(
            text = buttonTitle,
            onClick = { onButtonClick() },
            externalLink = externalLink
        )
    }
}

@Preview
@Composable
private fun MessagePreview() {
    GovUkTheme {
        Error(
            title = "Title",
            description = "Description",
            buttonTitle = "Button Title",
            onButtonClick = {}
        )
    }
}
