package uk.gov.govuk.design.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.R
import uk.gov.govuk.design.ui.extension.drawBottomStroke
import uk.gov.govuk.design.ui.extension.talkBackText
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun GovUkCardLegacy(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.cardBlue,
    borderColour: Color = GovUkTheme.colourScheme.strokes.cardBlue,
    padding: Dp = GovUkTheme.spacing.medium,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.listSelected
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
fun HomeNavigationCardLegacy(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSuppressClick: (() -> Unit)? = null,
    isSelected: Boolean = false,
    @DrawableRes icon: Int? = null,
    description: String? = null
) {
    GovUkCardLegacy(
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
fun HomeAlertCardLegacy(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    linkTitle: String?,
    linkUrl: String?,
    onClick: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    onSuppressClick: (() -> Unit)? = null
) {
    GovUkCardLegacy(
        modifier = modifier,
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
            if (linkTitle != null && linkUrl != null) {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = GovUkTheme.colourScheme.strokes.cardAlert
                    )

                    MediumVerticalSpacer()

                    val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
                    BodyRegularLabel(
                        text = linkTitle,
                        color = GovUkTheme.colourScheme.textAndIcons.link,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable {
                                onClick()
                                launchBrowser(linkUrl)
                            }
                            .semantics {
                                contentDescription = "$linkTitle $opensInWebBrowser"
                            }
                    )
                    MediumVerticalSpacer()
                }
            }
        }
    }
}

@Composable
fun SearchResultCardLegacy(
    title: String,
    description: String?,
    url: String,
    onClick: (String, String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkCardLegacy(
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

@Composable
fun UserFeedbackCardLegacy(
    body: String,
    linkTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BodyRegularLabel(
            text = body,
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center
        )
        val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)
        BodyRegularLabel(
            text = linkTitle,
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable {
                    onClick()
                }
                .semantics {
                    contentDescription = "$linkTitle $opensInWebBrowser"
                },
            color = GovUkTheme.colourScheme.textAndIcons.link,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NonTappableCardLegacy(
    body: String,
    modifier: Modifier = Modifier
) {
    BodyRegularLabel(
        text = body,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(12.dp))
            .background(GovUkTheme.colourScheme.surfaces.cardNonTappable)
            .padding(16.dp),
        color = GovUkTheme.colourScheme.textAndIcons.secondary
    )
}

@Composable
fun CentredCardWithIconLegacy(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .talkBackText(title, description)
            .drawBottomStroke(
                colour = GovUkTheme.colourScheme.strokes.cardDefault,
                cornerRadius = GovUkTheme.numbers.cornerAndroidList
            ),
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list),
        onClick = onClick,
        content = {
            ExtraLargeVerticalSpacer()

            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = modifier
                    .fillMaxWidth()
                    .size(32.dp),
                tint = GovUkTheme.colourScheme.textAndIcons.icon
            )

            title?.let { title ->
                SmallVerticalSpacer()

                BodyBoldLabel(
                    text = title,
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) { }
                        .padding(horizontal = GovUkTheme.spacing.extraLarge),
                    textAlign = TextAlign.Center,
                )
            }

            description?.let { description ->
                SmallVerticalSpacer()

                BodyRegularLabel(
                    text = description,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) { }
                        .padding(horizontal = GovUkTheme.spacing.extraLarge),
                    textAlign = TextAlign.Center,
                )
            }

            ExtraLargeVerticalSpacer()
        }
    )
}

@Composable
fun NavigationCardLegacy(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null
) {
    val opensInWebBrowser = stringResource(R.string.opens_in_web_browser)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .talkBackText(title, description, opensInWebBrowser)
            .drawBottomStroke(
                colour = GovUkTheme.colourScheme.strokes.cardDefault,
                cornerRadius = GovUkTheme.numbers.cornerAndroidList
            ),
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.list),
        onClick = onClick,
        content = {
            MediumVerticalSpacer()

            description?.let { description ->
                BodyRegularLabel(
                    text = description,
                    color = GovUkTheme.colourScheme.textAndIcons.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics(mergeDescendants = true) { }
                        .padding(horizontal = GovUkTheme.spacing.medium)
                )

                SmallVerticalSpacer()
            }

            Title2BoldLabel(
                text = title,
                color = GovUkTheme.colourScheme.textAndIcons.link,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics(mergeDescendants = true) { }
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )

            MediumVerticalSpacer()
        }
    )
}

@Preview
@Composable
private fun HomeNavigationCardPreview() {
    GovUkTheme {
        HomeNavigationCardLegacy(
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
        HomeNavigationCardLegacy(
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
        HomeNavigationCardLegacy(
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
        HomeNavigationCardLegacy(
            title = "Card title",
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun HomeAlertCardPreview() {
    GovUkTheme {
        HomeAlertCardLegacy(
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
private fun SearchResultWithDescriptionPreview() {
    GovUkTheme {
        SearchResultCardLegacy(
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
        SearchResultCardLegacy(
            title = "Card title",
            description = null,
            url = "",
            onClick = { _, _ -> },
            launchBrowser = {}
        )
    }
}

@Preview
@Composable
private fun UserFeedbackCardPreview() {
    GovUkTheme {
        UserFeedbackCardLegacy("Card body", "A link description", {})
    }
}

@Preview
@Composable
private fun NonTappableCardPreview() {
    GovUkTheme {
        NonTappableCardLegacy("Card body")
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithTitleAndDescriptionPreview() {
    GovUkTheme {
        CentredCardWithIconLegacy(
            onClick = { },
            icon = R.drawable.ic_settings,
            title = "Card title",
            description = "Card secondary text that may go over multiple lines."
        )
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithTitlePreview() {
    GovUkTheme {
        CentredCardWithIconLegacy(
            onClick = { },
            icon = R.drawable.ic_settings,
            title = "Card title"
        )
    }
}

@Preview
@Composable
private fun CentredCardWithIconWithDescriptionPreview() {
    GovUkTheme {
        CentredCardWithIconLegacy(
            onClick = { },
            icon = R.drawable.ic_settings,
            description = "Card secondary text that may go over multiple lines."
        )
    }
}

@Preview
@Composable
private fun NavigationCardWithTitleAndDescriptionPreview() {
    GovUkTheme {
        NavigationCardLegacy(
            title = "Card title",
            description = "Card secondary text that may go over multiple lines.",
            onClick = {}
        )
    }
}

@Preview
@Composable
private fun NavigationCardWithoutDescriptionPreview() {
    GovUkTheme {
        NavigationCardLegacy(
            title = "Card title",
            onClick = {}
        )
    }
}
