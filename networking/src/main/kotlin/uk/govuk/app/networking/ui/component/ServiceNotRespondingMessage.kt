package uk.govuk.app.networking.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.R
import uk.govuk.app.networking.domain.Constants.GOV_UK_URL

@Composable
fun ServiceNotRespondingMessage(
    title: String? = null,
    description: String? = null,
    linkTitle: String? = null,
    onLinkClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val messageOnLinkClick = onLinkClick ?: {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(GOV_UK_URL)
        context.startActivity(intent)
    }

    Message(
        title = title ?: stringResource(R.string.service_not_responding_title),
        description = description ?: stringResource(R.string.service_not_responding_description),
        linkTitle = linkTitle ?: stringResource(R.string.go_to_the_gov_uk_website),
        hasExternalLink = true,
        onLinkClick = messageOnLinkClick
    )
}

@Preview
@Composable
private fun ServiceNotRespondingMessagePreview() {
    GovUkTheme {
        ServiceNotRespondingMessage(onLinkClick = {})
    }
}