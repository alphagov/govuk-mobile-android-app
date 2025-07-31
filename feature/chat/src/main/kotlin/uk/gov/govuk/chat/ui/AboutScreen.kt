package uk.gov.govuk.chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import dev.jeziellago.compose.markdowntext.MarkdownText
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.navigation.navigateToChat
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun AboutRoute(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    AboutScreen(
        onClick = {
            navController.navigateToChat()
        },
        modifier = modifier
    )
}

@Composable
private fun AboutScreen(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        ChildPageHeader(
            text = stringResource(id = R.string.about_header_text),
            onBack = onClick,
            modifier = Modifier
        )

        LazyColumn {
            item {
                MediumVerticalSpacer()
            }

            item {
                MarkdownText(
                    markdown = stringResource(id = R.string.about_content),
                    linkColor = GovUkTheme.colourScheme.textAndIcons.link,
                    style = markdownTextStyle(),
                    enableSoftBreakAddsNewLine = false,
                    enableUnderlineForLink = false,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = GovUkTheme.spacing.medium)
                )
            }
        }
    }
}

@Composable
private fun markdownTextStyle() = TextStyle(
    color = GovUkTheme.colourScheme.textAndIcons.primary,
    fontSize = GovUkTheme.typography.bodyRegular.fontSize,
    fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
    fontWeight = GovUkTheme.typography.bodyRegular.fontWeight
)
