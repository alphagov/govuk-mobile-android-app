package uk.govuk.app.design.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun SearchCard(
    title: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.card
        ),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(GovUkTheme.spacing.medium)
        ) {
            BodyRegularLabel(title)

            MediumVerticalSpacer()

            val searchBarText = stringResource(R.string.search_bar_text)

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(GovUkTheme.colourScheme.surfaces.searchBox)
                    .clickable {
                        onClick(searchBarText)
                    }
                    .padding(GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.secondary
                )

                MediumHorizontalSpacer()

                BodyRegularLabel(
                    text = searchBarText,
                    modifier = Modifier.fillMaxWidth(),
                    color = GovUkTheme.colourScheme.textAndIcons.secondary
                )
            }
        }
    }
}

@Composable
fun TopicCard(
    title: String,
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
                .padding(GovUkTheme.spacing.medium)
        ){
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(25.dp),
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
                    painterResource(R.drawable.ic_chevron),
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

@Preview
@Composable
private fun SearchCardPreview() {
    GovUkTheme {
        SearchCard(
            title = "Title",
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun TopicCardPreview() {
    GovUkTheme {
        TopicCard(
            title = "Title",
            modifier = Modifier.height(100.dp)
        )
    }
}