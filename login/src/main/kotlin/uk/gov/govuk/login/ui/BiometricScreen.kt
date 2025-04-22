package uk.gov.govuk.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.R

// Todo - make it internal and expose via navigation
@Composable
fun BiometricRoute(
    modifier: Modifier = Modifier
) {
    BiometricScreen(modifier)
}

@Composable
private fun BiometricScreen(
    modifier: Modifier = Modifier
) {
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
                IconRow()

                MediumVerticalSpacer()

                LargeTitleBoldLabel(
                    text = stringResource(R.string.login_biometrics_title),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_1),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_2),
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = stringResource(R.string.login_biometrics_description_3),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1F))
        }

        FixedDoubleButtonGroup(
            primaryText = stringResource(R.string.login_biometrics_button),
            onPrimary = { },
            secondaryText = stringResource(R.string.login_biometrics_skip_button),
            onSecondary = { }
        )
    }
}

@Composable
private fun IconRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_face),
            contentDescription = null
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.textAndIcons.secondary // Todo - should probably be a stroke colour
        )

        LargeHorizontalSpacer()

        Image(
            painter = painterResource(id = R.drawable.ic_fingerprint),
            contentDescription = null
        )

        LargeHorizontalSpacer()

        VerticalDivider(
            thickness = 2.dp,
            color = GovUkTheme.colourScheme.textAndIcons.secondary // Todo - should probably be a stroke colour
        )

        LargeHorizontalSpacer()

        Image(
            painter = painterResource(id = R.drawable.ic_iris),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BiometricPreview() {
    GovUkTheme {
        BiometricScreen()
    }
}