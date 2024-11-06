package uk.govuk.app.topics.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.MediumHorizontalSpacer
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SubheadlineRegularLabel
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.R

@Composable
fun TopicSelectionCard(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    // Todo - should probably introduce a default card in the design module
    OutlinedCard(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Column(
            modifier = Modifier
                .padding(GovUkTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(R.drawable.ic_topic_benefits),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.surfaces.icon
            )
            Title3BoldLabel("Benefits")
            SubheadlineRegularLabel("Claiming benefits, mananging your benefits")
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
                BodyRegularLabel(
                    text = "Select",
                    color = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
        }
    }
}

@Composable
fun TopicVerticalCard(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Column(
            Modifier
                .clickable { onClick() }
                .padding(GovUkTheme.spacing.medium)
        ){
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.surfaces.icon
            )
            MediumVerticalSpacer()
            Spacer(Modifier.weight(1f))
            Row {
                var baseline by remember { mutableFloatStateOf(0f) }
                fun updateBaseline(textLayoutResult: TextLayoutResult) {
                    baseline = textLayoutResult.size.height - textLayoutResult.lastBaseline
                }
                val baselinePadding = with(LocalDensity.current) { baseline.toDp() }

                BodyBoldLabel(
                    text = title,
                    modifier = Modifier.weight(1f),
                    onTextLayout = ::updateBaseline
                )
                SmallHorizontalSpacer()
                Icon(
                    painterResource(uk.govuk.app.design.R.drawable.ic_chevron),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .padding(bottom = baselinePadding),
                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                )
            }
        }
    }
}

@Composable
fun TopicHorizontalCard(
    @DrawableRes icon: Int,
    title: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier,
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Row(
            Modifier
                .clickable { onClick(title) }
                .padding(GovUkTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.surfaces.icon
            )

            MediumHorizontalSpacer()

            BodyBoldLabel(
                text = title,
                modifier = Modifier.weight(1f),
            )

            MediumHorizontalSpacer()

            Icon(
                painterResource(uk.govuk.app.design.R.drawable.ic_chevron),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
            )
        }
    }
}

@Preview
@Composable
private fun TopicSelectionCardPreview() {
    GovUkTheme {
        TopicSelectionCard(
            isSelected = false,
            modifier = Modifier.height(200.dp)
        )
    }
}

@Preview
@Composable
private fun TopicVerticalCardPreview() {
    GovUkTheme {
        TopicVerticalCard(
            icon = R.drawable.ic_topic_default,
            title = "Title",
            onClick = { },
            modifier = Modifier.height(120.dp)
        )
    }
}

@Preview
@Composable
private fun TopicHorizontalCardPreview() {
    GovUkTheme {
        TopicHorizontalCard(
            icon = R.drawable.ic_topic_default,
            title = "Title",
            onClick = { }
        )
    }
}