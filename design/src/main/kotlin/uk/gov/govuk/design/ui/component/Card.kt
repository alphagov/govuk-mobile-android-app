package uk.gov.govuk.design.ui.component

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
                .padding(GovUkTheme.spacing.medium)
        ) {
            content()
        }
    }
}

@Composable
fun GovUkBottomBorderCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColour: Color = GovUkTheme.colourScheme.surfaces.cardDefault,
    borderColour: Color = GovUkTheme.colourScheme.strokes.cardDefault,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColour = if (isSelected) {
        GovUkTheme.colourScheme.surfaces.cardSelected
    } else {
        backgroundColour
    }

    val cornerRadius = 10.dp
    val strokeWidth = 3.dp
    val density = if (LocalDensity.current.density == 0f) 1f else LocalDensity.current.density
    val bottomPadding = strokeWidth / density

    Card(
        modifier = modifier
            .padding(bottom = bottomPadding)
            .drawBehind {

                // Bottom line
                drawLine(
                    color = borderColour,
                    start = Offset(x = cornerRadius.toPx(), y = size.height),
                    end = Offset(x = size.width - cornerRadius.toPx(), y = size.height),
                    strokeWidth = strokeWidth.toPx()
                )

                // Bottom-left corner
                drawArc(
                    color = borderColour,
                    startAngle = 180f,
                    sweepAngle = -90f,
                    useCenter = false,
                    topLeft = Offset(x = 7f, y = size.height - cornerRadius.toPx() * 2),
                    size = Size(cornerRadius.toPx() * 2, cornerRadius.toPx() * 2),
                    style = Stroke(width = strokeWidth.toPx())
                )

                // Bottom-right corner
                drawArc(
                    color = borderColour,
                    startAngle = 360f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(
                        x = (size.width - cornerRadius.toPx() * 2) - 7,
                        y = size.height - cornerRadius.toPx() * 2
                    ),
                    size = Size(cornerRadius.toPx() * 2, cornerRadius.toPx() * 2),
                    style = Stroke(width = strokeWidth.toPx())
                )
            },

        colors = CardDefaults.cardColors(containerColor = cardColour),
    ) {

        Column(
            modifier = Modifier
                .clickable(
                    enabled = onClick != null,
                    onClick = { onClick?.invoke() }
                )
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
fun HomeNavigationCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasChevron: Boolean = false,
    titleText: String? = null,
    descriptionText: String? = null,
    secondaryText: String? = null,
    callToActionBodyText: String? = null,
    callToActionLargeText: String? = null,
    @DrawableRes imageLeft: Int? = null,
    @DrawableRes imageRight: Int? = null,
    @DrawableRes imageTop: Int? = null,
    onSuppressClick: (() -> Unit)? = null
) {
    GovUkBottomBorderCard(
        modifier = modifier,
        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                imageTop?.let { image ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = null,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    imageLeft?.let { image ->
                        Image(
                            painter = painterResource(id = image),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()
                    ) {
                        titleText?.let { title ->
                            BodyBoldLabel(title)
                        }
                        descriptionText?.let { description ->
                            SmallVerticalSpacer()
                            BodyRegularLabel(description)
                        }
                        secondaryText?.let { secondary ->
                            SmallVerticalSpacer()
                            BodyRegularLabel(
                                text = secondary,
                                color = GovUkTheme.colourScheme.textAndIcons.secondary
                            )
                        }
                        callToActionBodyText?.let { callToActionBody ->
                            SmallVerticalSpacer()
                            BodyRegularLabel(
                                text = callToActionBody,
                                color = GovUkTheme.colourScheme.textAndIcons.link
                            )
                        }
                        callToActionLargeText?.let { callToActionLarge ->
                            SmallVerticalSpacer()
                            Title2BoldLabel(
                                text = callToActionLarge,
                                color = GovUkTheme.colourScheme.textAndIcons.link
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End
            ) {
                onSuppressClick?.let {
                    Row(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                onSuppressClick()
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_cancel),
                            contentDescription = "${stringResource(R.string.content_desc_remove)} ${titleText ?: ""}",
                        )
                    }
                }

                imageRight?.let { image ->
                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(end = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
                if (hasChevron) {
                    Icon(
                        painterResource(R.drawable.ic_chevron),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.icon,
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(end = 16.dp)
                            .weight(1f, fill = false)
                            .fillMaxHeight()
                    )
                }
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
private fun HomeNavigationCardLegacyPreview() {
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
private fun HomeNavigationCardLegacySuppressiblePreview() {
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
private fun HomeNavigationCardLegacyNoDescriptionPreview() {
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
private fun HomeNavigationCardLegacyNoIconPreview() {
    GovUkTheme {
        HomeNavigationCardLegacy(
            title = "Card title",
            onClick = { }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardPreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            secondaryText = "Card secondary text that may go over multiple lines",
            callToActionBodyText = "Call to action body text that may go over multiple lines"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardTopImagePreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            secondaryText = "Card secondary text that may go over multiple lines",
            callToActionBodyText = "Call to action body text that may go over multiple lines",
            imageTop = R.drawable.crown
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardLeftImagePreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            secondaryText = "Card secondary text that may go over multiple lines",
            callToActionBodyText = "Call to action body text that may go over multiple lines",
            imageLeft = R.drawable.crown
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardRightImagePreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            secondaryText = "Card secondary text that may go over multiple lines",
            callToActionBodyText = "Call to action body text that may go over multiple lines",
            imageRight = R.drawable.crown
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardSuppressablePreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            onSuppressClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardChevronPreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            descriptionText = "Card description text that may go over multiple lines",
            hasChevron = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F8FB)
@Composable
private fun HomeNavigationCardLargeCallToActionPreview() {
    GovUkTheme {
        HomeNavigationCard(
            onClick = { },
            titleText = "Card title text",
            callToActionLargeText = "Call to action text",
            imageRight = R.drawable.crown
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
