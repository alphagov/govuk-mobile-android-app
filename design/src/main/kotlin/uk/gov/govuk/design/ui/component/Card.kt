package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun GovUkCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.cardBlue,
    borderColour: Color = GovUkTheme.colourScheme.strokes.cardBlue,
    padding: Dp = GovUkTheme.spacing.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.cardSelected
    } else {
        backgroundColour
    }

    val strokeColour = if (isSelected) {
        GovUkTheme.colourScheme.strokes.cardSelected
    } else {
        borderColour
    }

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardColour),
        border = BorderStroke(
            width = 1.dp,
            color = strokeColour
        )
    ) {
        Column(
            modifier = Modifier
                .clickable(
                    enabled = onClick != null,
                    onClick = { onClick?.invoke() }
                )
                .padding(padding)
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
                Icon(
                    painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = GovUkTheme.colourScheme.textAndIcons.icon
                )
                SmallHorizontalSpacer()
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
                        tint = GovUkTheme.colourScheme.textAndIcons.icon,
                        modifier = Modifier.clickable { onSuppressClick() }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } ?: run {
                    Icon(
                        painterResource(R.drawable.ic_chevron),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.icon
                    )
                }
            }
        }
    }
}

@Composable
fun HomeAlertCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    linkTitle: String,
    linkUrl: String,
    onClick: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    onSuppressClick: (() -> Unit)? = null
) {
    GovUkCard(
        modifier = modifier,
        onClick = {
            onClick()
        },
        backgroundColour = GovUkTheme.colourScheme.surfaces.cardDefault,
        borderColour = GovUkTheme.colourScheme.strokes.cardAlert,
        padding = 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                    ) {
                        MediumVerticalSpacer()
                        title?.let { title ->
                            BodyBoldLabel(
                                title,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        if (title != null && description != null) {
                            SmallVerticalSpacer()
                        }

                        description?.let { description ->
                            BodyRegularLabel(
                                description,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        MediumVerticalSpacer()
                    }
                }
            }
            onSuppressClick?.let {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            onSuppressClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_cancel),
                        contentDescription = "${stringResource(R.string.content_desc_remove)} ${title ?: ""}",
                        tint = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = GovUkTheme.colourScheme.strokes.cardAlert
                )

                MediumVerticalSpacer()

                BodyRegularLabel(
                    text = linkTitle,
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable {
                            launchBrowser(linkUrl)
                        }
                )
                MediumVerticalSpacer()
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    description: String?,
    url: String,
    onClick: (String, String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkCard(
        modifier = modifier,
        onClick = {
            onClick(title, url)
            launchBrowser(url)
        }
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            BodyRegularLabel(
                text = title,
                modifier = Modifier.weight(1f),
                color = GovUkTheme.colourScheme.textAndIcons.link,
            )

            Icon(
                painter = painterResource(
                    R.drawable.ic_external_link
                ),
                contentDescription = stringResource(
                    R.string.opens_in_web_browser
                ),
                tint = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
            )
        }

        if (!description.isNullOrBlank()) {
            SmallVerticalSpacer()
            BodyRegularLabel(description)
        }
    }
}

@Preview
@Composable
private fun HomeAlertCardPreview() {
    GovUkTheme {
        HomeAlertCard(
            title = "Card title",
            description = "Card description that may go over multiple lines",
            onClick = { },
            launchBrowser = { },
            linkTitle = "A link description",
            linkUrl = "",
            onSuppressClick = { }
        )
    }
}


@Preview
@Composable
private fun HomeNavigationCardPreview() {
    GovUkTheme {
        HomeNavigationCard(
            title = "Card title",
            onClick = { },
            icon = R.drawable.ic_settings,
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
            icon = R.drawable.ic_settings,
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
            icon = R.drawable.ic_settings,
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

@Preview
@Composable
private fun SearchResultWithDescriptionPreview() {
    GovUkTheme {
        SearchResultCard(
            title = "Card title",
            description = "Description",
            url = "",
            onClick = { _, _ -> },
            launchBrowser = {}
        )
    }
}

@Preview
@Composable
private fun SearchResultWithoutDescriptionPreview() {
    GovUkTheme {
        SearchResultCard(
            title = "Card title",
            description = null,
            url = "",
            onClick = { _, _ -> },
            launchBrowser = {}
        )
    }
}
