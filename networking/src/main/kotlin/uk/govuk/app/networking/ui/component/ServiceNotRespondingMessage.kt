package uk.govuk.app.networking.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun ServiceNotRespondingMessage(
    title: String,
    description: String,
    buttonText: String,
    buttonDestination: String
) {
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(buttonDestination)
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.padding(
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.large,
                GovUkTheme.spacing.medium,
                0.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyBoldLabel(
                text = title,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        Row(
            Modifier.padding(
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.small,
                GovUkTheme.spacing.medium,
                GovUkTheme.spacing.medium
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyRegularLabel(
                text = description,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Row(
            Modifier
                .padding(GovUkTheme.spacing.medium)
                .clickable(onClick = { context.startActivity(intent) }),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            BodyRegularLabel(
                text = buttonText,
                modifier = Modifier.align(Alignment.CenterVertically),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(
                    uk.govuk.app.design.R.drawable.ic_external_link
                ),
                contentDescription = stringResource(
                    uk.govuk.app.design.R.string.opens_in_web_browser
                ),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.padding(start = GovUkTheme.spacing.small)
            )
        }
    }
}

@Preview
@Composable
private fun TestServiceNotRespondingMessage() {
    GovUkTheme {
        ServiceNotRespondingMessage("Test Title", "Test Description", "Test Button", "")
    }
}