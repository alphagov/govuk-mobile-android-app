package uk.gov.govuk.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.login.R

@Composable
internal fun BiometricRoute(
    modifier: Modifier = Modifier
) {

}

@Composable
private fun BiometricScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconRow()
        MediumVerticalSpacer()
        LargeTitleBoldLabel(
            text = "Unlock the app with biometrics",
            textAlign = TextAlign.Center
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