package uk.gov.govuk.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun SignOutConfirmationRoute(
    modifier: Modifier = Modifier
) {
    SignOutScreen(modifier)
}

@Composable
private fun SignOutScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        FullScreenHeader(
            onBack = { }
        )
        Column(Modifier
            .verticalScroll(rememberScrollState())
            .weight(1f)
            .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            LargeTitleBoldLabel("Are you sure you want to sign out?")

            MediumVerticalSpacer()

            BodyRegularLabel("This means:")

            MediumVerticalSpacer()

            Row {
                Title3BoldLabel("•    ")
                BodyRegularLabel("if you're using your fingerprint, face or iris to unlock the app, this will be turned off")
            }

            ExtraSmallVerticalSpacer()

            Row {
                Title3BoldLabel("•    ")
                BodyRegularLabel("you'll stop sharing statistics about how you use the app")
            }

            MediumVerticalSpacer()

            BodyRegularLabel("Next time you sign in, you'll be able to set these preferences again.")

            LargeVerticalSpacer()
        }

        FixedDoubleButtonGroup(
            primaryText = "Sign out",
            onPrimary = { },
            secondaryText = "Cancel",
            onSecondary = { },
            primaryDestructive = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SignOutPreview() {
    GovUkTheme {
        SignOutScreen()
    }
}