package uk.govuk.app.local.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
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
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
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
private fun LocalNavigationCard(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(R.string.local_title)

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.localBackground),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.localBorder
        )
    ) {
        Column(
            Modifier
                .clickable { onClick(title) }
                .padding(
                    top = GovUkTheme.spacing.small,
                    bottom = GovUkTheme.spacing.medium
                )
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .padding(start = GovUkTheme.spacing.medium),
                contentAlignment = Alignment.CenterStart
            ) {
                Title3BoldLabel(stringResource(R.string.local_section_title))
            }
            SmallVerticalSpacer()
            HorizontalDivider(color = GovUkTheme.colourScheme.strokes.localBorder)
            MediumVerticalSpacer()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = GovUkTheme.spacing.medium),
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
}

@Preview
@Composable
private fun LocalWidgetPreview() {
    GovUkTheme {
        LocalWidget(onClick = { })
    }
}
