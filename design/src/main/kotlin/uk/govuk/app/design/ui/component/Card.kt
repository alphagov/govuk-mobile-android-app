package uk.govuk.app.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun GovUkCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.cardSelected
    } else {
        GovUkTheme.colourScheme.surfaces.card
    }

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColour),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    enabled = onClick != null,
                    onClick = { onClick?.invoke() }
                )
                .padding(GovUkTheme.spacing.medium)
        ) {
            content()
        }
    }
}

@Composable
fun HomeNavigationCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSuppressClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    @DrawableRes icon: Int? = null,
    description: String? = null
) {
    GovUkCard(
        modifier = modifier,
        isSelected = isSelected,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Image(
                    painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                MediumHorizontalSpacer()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = GovUkTheme.spacing.medium)
            ) {
                BodyBoldLabel(title)
                description?.let {
                    ExtraSmallVerticalSpacer()
                    BodyRegularLabel(it)
                }
            }
            Column(
                horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                onSuppressClick?.let {
                    Icon(
                        painterResource(R.drawable.ic_cancel),
                        contentDescription = "${stringResource(R.string.content_desc_remove)} $title",
                        tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon,
                        modifier = Modifier.clickable { onSuppressClick() }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Icon(
                    painterResource(R.drawable.ic_chevron),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                )
            }
        }
    }
}

@Preview
@Composable
private fun HomeNavigationCardPreview() {
    GovUkTheme {
        HomeNavigationCard(
            title = "Card title",
            onClick = { },
            icon = R.drawable.ic_visited,
            description = "Card description that may go over multiple lines"
        )
    }
}

@Preview
@Composable
private fun HomeNavigationCardSuppressiblePreview() {
    GovUkTheme {
        HomeNavigationCard(
            title = "Card title",
            onClick = { },
            onSuppressClick = { },
            icon = R.drawable.ic_visited,
            description = "Card description that may go over multiple lines"
        )
    }
}

@Preview
@Composable
private fun HomeNavigationCardNoDescriptionPreview() {
    GovUkTheme {
        HomeNavigationCard(
            title = "Card title",
            onClick = { },
            icon = R.drawable.ic_visited,
        )
    }
}

@Preview
@Composable
private fun HomeNavigationCardNoIconPreview() {
    GovUkTheme {
        HomeNavigationCard(
            title = "Card title",
            onClick = { }
        )
    }
}