package uk.govuk.app.local.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.GovUkCard
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.R

@Composable
fun LocalWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    LocalNavigationCard(
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun LocalNavigationCard(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(R.string.local_title)

    GovUkCard(
        modifier = modifier,
        onClick = { onClick(title) },
        backgroundColour = GovUkTheme.colourScheme.surfaces.localBackground,
        borderColour = GovUkTheme.colourScheme.strokes.localBorder
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.outline_pin_drop_24),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.textAndIcons.localIcon
            )
            SmallHorizontalSpacer()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = GovUkTheme.spacing.medium)
            ) {
                BodyBoldLabel(title)
                ExtraSmallVerticalSpacer()
                BodyRegularLabel(
                    stringResource(R.string.local_description),
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
            Icon(
                painterResource(uk.gov.govuk.design.R.drawable.ic_chevron),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.localIcon
            )
        }
    }
}

@Preview
@Composable
private fun LocalWidgetPreview() {
    GovUkTheme {
        LocalWidget(onClick = { })
    }
}
