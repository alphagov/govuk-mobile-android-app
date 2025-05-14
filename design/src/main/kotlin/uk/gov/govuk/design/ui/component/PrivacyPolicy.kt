package uk.gov.govuk.design.ui.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import uk.gov.govuk.design.BuildConfig.PRIVACY_POLICY_URL
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme
import androidx.core.net.toUri

@Composable
fun PrivacyPolicyLink(
    modifier: Modifier = Modifier,
    onClick: ((text: String, url: String) -> Unit)? = null
) {
    val context = LocalContext.current
    val text = stringResource(R.string.privacy_policy_read_more)
    val url = PRIVACY_POLICY_URL
    Row(
        modifier
            .clickable {
                onClick?.invoke(text, url)
                openPrivacyPolicyInBrowser(context, url)
            }
    ) {
        BodyRegularLabel(
            text = text,
            color = GovUkTheme.colourScheme.textAndIcons.link,
            modifier = Modifier.weight(1f, fill = false)
        )
        SmallHorizontalSpacer()
        Icon(
            painter = painterResource(
                R.drawable.ic_external_link
            ),
            contentDescription = stringResource(R.string.opens_in_web_browser),
            tint = GovUkTheme.colourScheme.textAndIcons.link,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

private fun openPrivacyPolicyInBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = url.toUri()
    context.startActivity(intent)
}
