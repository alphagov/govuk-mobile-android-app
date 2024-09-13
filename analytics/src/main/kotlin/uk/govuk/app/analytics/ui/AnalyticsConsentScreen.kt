package uk.govuk.app.analytics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun AnalyticsConsentRoute(
    modifier: Modifier = Modifier
) {

}

@Composable
private fun AnalyticsConsentScreen(
    modifier: Modifier = Modifier
) {
    // Todo - scroll
    Column(
        modifier
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.large)
            .padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        Column(Modifier.weight(1f)) {
            LargeTitleBoldLabel("Share statistics about how you use the GOV.UK app")
            Spacer(Modifier.height(GovUkTheme.spacing.medium))
            BodyRegularLabel("You can help us improve this app by agreeing to share statistics about:")
            Spacer(Modifier.height(GovUkTheme.spacing.medium))
            BulletList()
            Spacer(Modifier.height(GovUkTheme.spacing.medium))
            BodyRegularLabel("These statistics are anonymous.")
            Spacer(Modifier.height(GovUkTheme.spacing.medium))
            BodyRegularLabel("You can stop sharing these statistics at any time by changing your app settings.")
            Spacer(Modifier.height(GovUkTheme.spacing.medium))
            PrivacyPolicyLink()
        }

        val enableButtonText = "Allow statistics sharing"
        val disableButtonText = "Don't allow statistics sharing"
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            HorizontalButtonGroup(
                primaryText = enableButtonText,
                onPrimary = { }, // Todo
                secondaryText = disableButtonText,
                onSecondary = {  } // Todo
            )
        } else {
            VerticalButtonGroup(
                primaryText = enableButtonText,
                onPrimary = {  }, // Todo
                secondaryText = disableButtonText,
                onSecondary = {  } // Todo
            )
        }
    }
}

@Composable
private fun BulletList(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        BulletItem("the pages you visit within the app")
        Spacer(Modifier.height(GovUkTheme.spacing.small))
        BulletItem("how long you spend on each page")
        Spacer(Modifier.height(GovUkTheme.spacing.small))
        BulletItem("what you tap on while you're on each page")
        Spacer(Modifier.height(GovUkTheme.spacing.small))
        BulletItem("errors that happen")
    }
}

@Composable
private fun BulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(GovUkTheme.colourScheme.textAndIcons.primary)
        )
        Spacer(Modifier.width(GovUkTheme.spacing.medium))
        BodyRegularLabel(text)
    }
}

@Composable
private fun PrivacyPolicyLink(
    modifier: Modifier = Modifier
) {
    // Todo - handle click
    Row(modifier) {
        BodyRegularLabel(
            text = "Read more about this in the privacy policy",
            color = GovUkTheme.colourScheme.textAndIcons.link,
        )
        Spacer(Modifier.width(GovUkTheme.spacing.small
        ))
        Icon(
            painter = painterResource(
                uk.govuk.app.design.R.drawable.baseline_open_in_new_24
            ),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.link
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AnalyticsConsentPreview() {
    GovUkTheme {
        AnalyticsConsentScreen()
    }
}