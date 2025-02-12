package uk.govuk.app.topics.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.GovUkCard
import uk.govuk.app.design.ui.component.MediumHorizontalSpacer
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SubheadlineRegularLabel
import uk.govuk.app.design.ui.component.Title3BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.R

@Composable
fun TopicSelectionCard(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    GovUkCard (
        modifier = modifier,
        isSelected = isSelected,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = GovUkTheme.colourScheme.surfaces.icon
            )
            Title3BoldLabel(
                text = title,
                textAlign = TextAlign.Center
            )
            SubheadlineRegularLabel(
                text = description,
                textAlign = TextAlign.Center
            )
            MediumVerticalSpacer()
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                SelectedButton()
            } else {
                SelectButton()
            }
        }
    }
}

@Composable
private fun SelectButton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.link
        )
        BodyRegularLabel(
            text = stringResource(R.string.selectButton),
            modifier = Modifier.padding(top = 2.dp),
            color = GovUkTheme.colourScheme.textAndIcons.link
        )
    }
}

@Composable
private fun SelectedButton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.buttonSuccess
        )
        Spacer(Modifier.width(2.dp))
        BodyRegularLabel(
            text = stringResource(R.string.selectedButton),
            modifier = Modifier.padding(top = 2.dp),
            color = GovUkTheme.colourScheme.textAndIcons.buttonSuccess
        )
    }
}

@Composable
fun TopicVerticalCard(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkCard(
        modifier,
        onClick = onClick
    ) {
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
                tint = GovUkTheme.colourScheme.textAndIcons.icon
            )
        }
    }
}

@Composable
fun TopicHorizontalCard(
    @DrawableRes icon: Int,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkCard(
        modifier,
        onClick = { onClick() }
    ) {
        Row(
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
                tint = GovUkTheme.colourScheme.textAndIcons.icon
            )
        }
    }
}

@Preview
@Composable
private fun TopicSelectionCardUnselectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            description = "Claiming benefits, managing your benefits",
            isSelected = false,
            onClick = { },
            modifier = Modifier.height(200.dp)
        )
    }
}

@Preview
@Composable
private fun TopicSelectionCardSelectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            description = "Claiming benefits, managing your benefits",
            isSelected = true,
            onClick = { },
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