package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun ErrorPage(
    headerText: String,
    subText: String,
    buttonText: String,
    onBack: (String) -> Unit,
    modifier: Modifier = Modifier,
    additionalText: String? = null,
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_error),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier.height(IntrinsicSize.Min)
                        .padding(all = GovUkTheme.spacing.medium)
                )

                LargeHorizontalSpacer()

                LargeTitleBoldLabel(
                    text = headerText,
                    textAlign = TextAlign.Center
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = subText,
                    textAlign = TextAlign.Center
                )

                if (additionalText != null) {
                    MediumVerticalSpacer()

                    BodyRegularLabel(
                        text = additionalText,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.weight(1F))
        }

        FixedPrimaryButton(
            text = buttonText,
            onClick = { onBack(buttonText) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithoutAdditionalTextPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = "Sub text",
            buttonText = "Button text",
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPageWithAdditionalTextPreview() {
    GovUkTheme {
        ErrorPage(
            headerText = "Header text",
            subText = "Sub text",
            additionalText = "Additional text",
            buttonText = "Button text",
            onBack = {}
        )
    }
}
